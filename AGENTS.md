# AGENTS.md

本文件是**跨工具入口**（Codex / Cursor / Copilot / Jules 等默认只读根目录 `AGENTS.md`）。
工程开发规范、领域知识、技能流程的正文统一在 [`.ai/`](.ai/README.md)，本文件不复制正文，只给入口与最小必备约束。

## 项目定位

DJI Cloud API 上云示例服务：Cloud API（Java 11 / Spring Boot 2.7.12 / Maven 多模块）+ MySQL + Redis + MQTT + MinIO。
设备侧（机场、飞机）通过 MQTT `thing/product/...` 主题与云端交互。

## 目录速览

| 路径 | 说明 |
| --- | --- |
| `cloud-sdk/` | 上云 API SDK：报文模型、枚举、MQTT 基础设施。不放业务逻辑 |
| `sample/` | 示例服务（业务域：manage / wayline / media / map / livestream / psdk / storage ...） |
| `sql/cloud_sample.sql` | 全量表结构脚本（唯一入口，按 `# 表名` 分节） |
| `.ai/` | 开发规范、领域知识、技能流程、架构索引（**改动前先读**） |
| `.drone.yml` / `Dockerfile` | 双架构镜像 CI（规范见 `.ai/rules/cicd-multiarch-build.md`） |

## 必读顺序

1. [`.ai/README.md`](.ai/README.md)：目录分类与维护约定。
2. [`.ai/rules/`](.ai/rules)：编码与分层、数据库变更、CICD、目录约定（强制）。
3. 改具体业务（如航线/飞控）：先读 [`.ai/knowledge/`](.ai/knowledge) 对应文档与 [`.ai/context/architecture.md`](.ai/context/architecture.md)。
4. 按既有套路改造：参考 [`.ai/skills/`](.ai/skills)。

## 最小必备约束

- 表结构只改 `sql/cloud_sample.sql`，并同步 `sample/.../model/entity` 实体（生产环境另出 `ALTER TABLE`）。
- 新增数据表需补齐：实体、DTO、`dao/I<Xxx>Mapper`、`service/I<Xxx>Service`、`service/impl/<Xxx>ServiceImpl`。
- 不修改已有方法签名、请求/返回报文字段与已有日志/提示字符串（外部系统会解析）。
- 涉及设备协议、报文、枚举以 `cloud-sdk` 中的定义为准，不自行新增字面量映射。
- 规范类结论写进 `.ai/`，不要只留在对话或提交说明里。

## 构建

系统默认 JDK 可能是 1.8，**必须显式指定 JDK 11**：

```powershell
$env:JAVA_HOME='C:\Java\jdk-11.0.20'; $env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn -pl sample -am compile "-DskipTests"
```

## 协作约束

- 未经用户明确要求，不执行 `git commit` / `git push`；远程推送前必须确认。
