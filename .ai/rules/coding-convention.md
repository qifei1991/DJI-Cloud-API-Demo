# 编码与分层规范

## 模块与包结构

- `cloud-sdk`：设备协议与云 API 模型（MQTT、REST 报文、枚举），业务逻辑不要写在这里。
- `sample`：示例服务，`com.dji.sample.<业务域>`，单个业务域内固定分层：
  - `controller`：HTTP 入口，只做参数校验与结果包装（`HttpResultResponse`）。
  - `service` + `service/impl`：业务逻辑，接口命名 `I<Xxx>Service`，实现 `<Xxx>ServiceImpl`。
  - `dao`：MyBatis-Plus `BaseMapper<T>` 接口，命名 `I<Xxx>Mapper`；
    启动类已配置 `@MapperScan("com.dji.sample.*.dao")`，新增 mapper 无需改扫描配置。
  - `model/entity`：与表一一对应的实体；`model/dto`：服务内部传递对象；`model/param`：接口入参；`model/enums`：业务枚举。

## 实体与服务

- 实体使用 `@Data @TableName("表名") @Builder @AllArgsConstructor @NoArgsConstructor`，主键 `@TableId(type = IdType.AUTO)`，每个字段显式写 `@TableField("列名")`。
- `create_time` / `update_time` 用 `long` 毫秒时间戳 + `@TableField(fill = FieldFill.INSERT / INSERT_UPDATE)`，由 `MybatisPlusMetaObjectHandler` 自动填充，不要在业务代码里手动赋值。
- 业务服务统一 `@Service @Slf4j @Transactional`（只读场景可另议），异常抛出用中文提示，保持既有风格：
  - 数据/文件缺失：`SQLException`（沿用现有航线、媒体等服务的习惯）；
  - 参数非法或状态不符：`IllegalArgumentException` / `RuntimeException`。
- Service 接口的 Javadoc 用英文一句话说明职责，实现类内部逻辑可用中文注释说明业务约束。

## 对象存储

- 统一通过 `OssServiceContext` + `OssConfiguration.bucket` 读写对象；目录前缀取 `OssConfiguration.objectDirPrefix`，不要在代码里硬编码环境地址。
- 对象 key 采用 `{objectDirPrefix}/{业务目录}/{文件名}` 形式，新增业务目录时在 `KmzFileProperties` 一类的常量类中定义目录名。
- `IOssService#putObject` 在对象已存在时会抛“文件名已存在”，因此副本类文件必须带时间戳/UUID 保证 key 唯一；该方法内部吞掉上传异常，生成副本后必须用返回值（已计算 sign）与后续下发配合验证，必要时排查 MinIO 日志。

## 功能一致性

- 优化/重构时保持原有方法签名、请求报文和返回结构不变，不修改已有日志与提示字符串，避免前端和外部系统（无人机管理系统）解析失败。
- 对接 `cloud-sdk` 枚举时用 `Enum.find(...)`，不要自行新增字面量映射。
