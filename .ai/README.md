# .ai —— AI 开发资料目录

本目录存放本项目的 AI 辅助开发资料（规范、领域知识、技能流程），供人和 AI 共同维护。
与代码同仓提交，随分支演进；新增结论优先写进这里，而不是只留在对话里。

## 目录分类

| 目录 | 内容 | 典型文件 |
| --- | --- | --- |
| `.ai/rules/` | 强制规范与约束（编码、分层、数据库变更、CICD 等），违反即视为缺陷 | `coding-convention.md`、`database-change.md`、`cicd-multiarch-build.md`、`ai-docs-convention.md` |
| `.ai/knowledge/` | 领域知识：业务背景、字段含义、报文/文件结构、参数映射 | `wayline-file-copy.md` |
| `.ai/skills/` | 可复用的操作流程（How-to）：改一个功能要动哪些文件、如何自测 | `wayline-file-copy.md` |
| `.ai/context/` | 工程架构与关键类索引，帮助快速定位代码 | `architecture.md` |

## 维护约定

1. 新增一个业务功能时，至少补一份 `knowledge/` 文档；有固定改造套路时再补 `skills/` 文档。
2. 文档只写“与代码不一致就会踩坑”的信息，不复述通用 Java/Spring 知识。
3. 涉及表结构、上报报文、设备端约定的内容必须与 `sql/cloud_sample.sql`、`cloud-sdk` 中的定义保持一致。
4. 工程开发规范（编码、数据库、CICD、目录约定等）全部放 `.ai/`；`.qoder/` 只保留 Qoder 工具自身使用说明（自定义命令、工具规则），需要工程规范时用链接引用 `.ai/` 对应文档，不复制正文。
5. 文档中的类名/表名用真实名称书写，便于全文检索。
6. 仓库根目录 `AGENTS.md` 仅作为跨工具入口（Codex/Cursor/Copilot 等只读该文件），内容为项目定位、目录速览、最小必备约束和指向 `.ai/` 的链接，不写正文。
