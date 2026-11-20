# 航线文件副本（失联动作覆盖）领域知识

## 背景与问题

大疆上云 API 的 `flighttask_prepare` 报文里的 `out_of_control` / `exit_wayline_when_rc_lost` 只是辅助字段，
飞机实际执行的是 **航线文件（KMZ）内 `wpml:missionConfig` 中写死的失联动作**。
因此：前端在下发飞行计划时选择的失联动作，只有在航线文件里被同步改写后才真正生效。

## 方案总览

1. `wayline_job` 仍按原逻辑保存飞行计划参数（`out_of_control`、`exit_wayline_when_rc_lost` 等）。
2. 任务准备阶段 `FlightTaskServiceImpl.prepareFlightTask` 判断：`exit_wayline_when_rc_lost` 非空时，
   调用 `IWaylineFileFlightService#getOrCreateFlightFile` 生成/复用航线文件副本。
3. 副本生成流程：MinIO 下载原始 KMZ → 解压 → 改写 `wpmz/template.kml` 与 `wpmz/waylines.wpml` 的失联动作节点
   → 重新压缩 → 计算 MD5 → 以 `{原文件名}_{毫秒时间戳}` 作为副本名上传 MinIO → 写入 `wayline_file_flight`。
4. 下发时用副本的 URL + 副本 MD5 作为 `FlighttaskFile`，`flight_id` 仍是原 `job_id`。
5. 改写逻辑抽到 `com.dji.sample.wayline.util.KmzFileUtils`（纯内存函数，不碰存储与数据库），线上服务与本地测试 `KmzFileUtilsLocalTest` 共用同一份实现。

## 表 `wayline_file_flight`

字段与 `wayline_file` 对齐，去掉航线模板相关字段，新增两个关联字段：

| 列 | 含义 |
| --- | --- |
| `id` | 自增主键 |
| `name` | 副本名称：`{原文件名}_{毫秒时间戳}`，与原始航线名区分 |
| `file_id` | 副本自身的 uuid |
| `original_file_id` | 原始航线的 `wayline_file.wayline_id` |
| `job_id` | 使用该副本的飞行计划 `wayline_job.job_id`，副本随任务唯一 |
| `object_key` | MinIO 对象 key：`{oss.object-dir-prefix}/wayline_file_copy/{副本名}.kmz` |
| `sign` | 副本 KMZ 的 MD5，作为下发的 `fingerprint` |
| `user_name` | 创建人（优先取任务创建人） |
| `create_time` / `update_time` | 毫秒时间戳，自动填充 |

索引：`file_id`、`job_id` 唯一键，`original_file_id` 普通索引（一个任务一份副本，重复准备直接复用；并发插入由唯一键兜底）。

## KMZ 文件结构

```
{name}.kmz
└── wpmz/
    ├── template.kml       # 模板：航点、航线参数，含 wpml:missionConfig
    ├── waylines.wpml      # 实际执行文件，含 wpml:missionConfig
    └── res/               # 资源（如开启 3D 飞行时的纹理）
```

`missionConfig` 中与本需求相关的节点：

| 节点 | 取值 | 含义 |
| --- | --- | --- |
| `wpml:exitOnRCLost` | `goContinue` / `executeLostAction` | 继续执行航线 / 退出航线，执行失控动作 |
| `wpml:executeRCLostAction` | `goBack` / `hover` / `landing` | 飞机失联后的动作 |

## 参数映射

| 飞行计划参数 | 取值 | 写入节点 |
| --- | --- | --- |
| `wayline_job.exit_wayline_when_rc_lost`（`ExitWaylineWhenRcLostEnum`） | `0 CONTINUE` | `wpml:exitOnRCLost` = `goContinue` |
| 同上 | `1 EXECUTE_RC_LOST_ACTION` | `wpml:exitOnRCLost` = `executeLostAction` |
| `wayline_job.out_of_control`（`OutOfControlActionEnum`） | `0 RETURN_TO_HOME` | `wpml:executeRCLostAction` = `goBack` |
| 同上 | `1 HOVERING` | `wpml:executeRCLostAction` = `hover` |
| 同上 | `2 LANDING` | `wpml:executeRCLostAction` = `landing` |
| `out_of_control` 为空 | — | 不改写 `executeRCLostAction`，保留原文件取值 |

## 已知边界

- **蛙跳任务（多机场）同样使用副本**：`prepareMultiDockFlightTask` 与单机场共用 `getFlightTaskFile`，起降两个机场会下发同一份副本的 URL 与 MD5。
- 断点续飞（`createWaylineJobByParent`）会新建 `job_id`，因此会再生成一份副本（失联动作与该次任务参数一致，符合预期）。
- 节点缺失时：`missionConfig` 不存在则跳过改写并告警；`exitOnRCLost` / `executeRCLostAction` 缺失则按同级节点命名空间新建。
- 非标准 KMZ（条目不是 `wpmz/template.kml`、`wpmz/waylines.wpml`，如多一层外层目录）不再让任务准备失败：只 `log.warn` 并沿用原文件内容，副本与原始文件一致（失联动作不生效），需按日志排查条目路径。
- XML 解析已禁用 DOCTYPE 与外部实体（防 XXE）：带 DOCTYPE 声明的航线文件会直接准备失败。
- 事务边界：MinIO 下载/上传/删除与解压、压缩全部在事务之外，只有 `insert` / `delete` 通过 `TransactionTemplate` 单独开启事务。
- KMZ 只能用 UTF-8 编码（沿用 `WaylineFileServiceImpl#validKmzFile` 的编码校验结论）。
- 时间戳命名保证对象 key 唯一；`IOssService#putObject` 对同名对象会直接抛“文件名已存在”。
- `IOssService#putObject` 上传失败只记日志不抛异常，所以生成副本后必须回读校验长度；校验失败或入库失败时删除本次对象并抛 `SQLException`，避免“表里有记录、桶里没文件”。
- 副本与任务一对一（`job_id` 唯一键）：并发插入冲突时删除本次对象、复用已有记录（`DuplicateKeyException` 分支）。
- 副本随任务删除：`WaylineJobServiceImpl#deleteJob` 联动 `deleteFlightFileByJobId` 删除表记录与 MinIO 对象；原始航线文件与任务无关，**不随任务删除**。
