# 数据库变更规范

## 唯一入口

- 全量表结构脚本：`sql/cloud_sample.sql`，按 `# 表名` 分节，模式固定为 `DROP TABLE IF EXISTS` + `CREATE TABLE`。
- 新增/修改表时直接改这一份脚本，并把新表放在逻辑相邻的位置（航线相关表集中在 `wayline_file` / `wayline_file_flight` / `wayline_job` 附近）。
- 已上线环境的增量变更需另行提供 `ALTER TABLE` 语句，**不要**用 `DROP + CREATE` 去更新生产库。

## 字段约定

- 引擎与字符集：`ENGINE=InnoDB DEFAULT CHARSET=utf8mb3`，与既有表保持一致（历史表用 `utf8` + `utf8_general_ci`，新增字段跟表保持一致即可）。
- 主键：`id int unsigned NOT NULL AUTO_INCREMENT`。
- 业务 ID：`uuid` 字符串统一 `varchar(64)`，并在需要按 ID 查询的表上建唯一键，命名 `<列名>_UNIQUE`。
- 外键关联列（如 `job_id`、`original_file_id`）不建外键约束，改为普通索引 `KEY <列名>_INDEX`。
- 时间列：`bigint` 毫秒时间戳，`create_time` / `update_time` 均 `NOT NULL`。
- 每个列必须有 `COMMENT`，注释用英文描述业务含义；中文说明放在 `.ai/knowledge/` 文档里。

## 实体同步

- 表结构变更后必须同步 `sample/.../model/entity` 下的实体：`@TableName`、`@TableField("列名")` 一一对齐。
- 新增表时同时补齐：实体、`model/dto`、`dao/I<Xxx>Mapper`、`service/I<Xxx>Service`、`service/impl/<Xxx>ServiceImpl`。
- 字段命名在 Java 侧用驼峰（`job_id` → `jobId`），`user_name` 列在实体中沿用既有约定映射到 `username` 属性。
