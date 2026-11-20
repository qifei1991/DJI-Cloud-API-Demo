# 工程架构与关键类索引

## 模块

- `cloud-sdk`：上云 API SDK。`com.dji.sdk.cloudapi.<业务域>` 定义 MQTT/REST 报文模型与枚举，
  `com.dji.sdk.mqtt` 是订阅/发布基础设施，`com.dji.sdk.config.version` 管理云端与设备版本的兼容性。
- `sample`：示例云服务（Spring Boot 2.7 + MyBatis-Plus + Redis + MinIO），业务域包：
  `manage`（设备/机场）、`wayline`（航线与飞行计划）、`media`、`map`、`livestream`、`psdk`、`storage`、`control` 等。
- 配置文件：`sample/src/main/resources/application.yml`（MySQL/Redis/MQTT/MinIO/业务开关）；
  生产环境 `application*.yml` 不进 jar，外置到 `/app/config/application.yml`。

## wayline 业务域

| 分类 | 关键类 | 职责 |
| --- | --- | --- |
| controller | `WaylineFileController` / `WaylineJobController` | 航线文件与飞行计划的 HTTP 接口 |
| controller | `cloudapi/controller/WaylineApiController` | 供无人机管理系统调用/上报的接口 |
| service | `IFlightTaskService` / `FlightTaskServiceImpl` | 任务下发：`prepareFlightTask`（准备）、`executeFlightTask`（执行）、条件/定时/断点续飞调度 |
| service | `IWaylineFileService` / `WaylineFileServiceImpl` | 航线文件 CRUD、KMZ 校验、对象存储读写 |
| service | `IWaylineFileFlightService` / `WaylineFileFlightServiceImpl` | 航线文件副本：改写失联动作、上传、随任务复用 |
| service | `IWaylineJobService` / `WaylineJobServiceImpl` | `wayline_job` 持久化与状态维护 |
| dao | `IWaylineFileMapper` / `IWaylineJobMapper` / `IWaylineFileFlightMapper` | MyBatis-Plus Mapper |
| model | `WaylineFileEntity` / `WaylineJobEntity` / `WaylineFileFlightEntity` | 表实体 |
| model | `KmzFileProperties` | KMZ 目录、节点名、副本目录等常量 |

## 飞行任务链路（dock 单机场）

1. `WaylineJobController` → `FlightTaskServiceImpl#publishFlightTask`：按 `taskDays/taskPeriods` 生成 `wayline_job`。
2. `publishCommonOneFlightTask` → `prepareFlightTask`：
   - 取航线文件（`wayline_file`），必要时生成航线文件副本（`wayline_file_flight`）；
   - 组装 `FlighttaskPrepareRequest`，通过 MQTT `flighttask_prepare` 下发到机场（`SDKWaylineService`）。
3. 立即任务随即 `executeFlightTask`；定时任务写入 Redis zset（`RedisConst.WAYLINE_JOB_TIMED_EXECUTE`）由 `checkScheduledJob` 触发；
   条件任务由 `prepareConditionJob` 轮询准备。
4. 多机场（蛙跳）走 `prepareMultiDockFlightTask` / `executeMultiDockFlightTask`，暂未接入航线文件副本。

## 约定

- 设备侧调用统一走 `SDKManager.getDeviceSDK(sn)` + `cloud-sdk` 的 `AbstractXxxService`，不要自己在业务层拼 topic。
- 设备在线状态、OSD 等实时数据从 `IDeviceRedisService` 读取，不要查库。
- 对象存储统一 `OssServiceContext`；Redis 统一 `RedisOpsUtils` / `IWaylineRedisService`。
