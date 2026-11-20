# 技能：下发飞行计划时覆盖航线文件失联动作

## 适用场景

需要让“前端选择的失联动作”真正下发到飞机，而不是沿用航线文件里写死的值。

## 改动清单（按顺序）

1. 表结构：`sql/cloud_sample.sql` 增加 `wayline_file_flight`。
2. 持久层：
   - `sample/.../wayline/model/entity/WaylineFileFlightEntity.java`
   - `sample/.../wayline/model/dto/WaylineFileFlightDTO.java`
   - `sample/.../wayline/dao/IWaylineFileFlightMapper.java`
3. 业务层：
   - `sample/.../wayline/service/IWaylineFileFlightService.java`
   - `sample/.../wayline/service/impl/WaylineFileFlightServiceImpl.java`
     - `getFlightFileByJobId`：按 `job_id` 查副本（取 id 最大的一条）。
     - `getOrCreateFlightFile`：存在即复用；不存在则下载原始 KMZ、改写失联动作、上传、回读校验、入库；上传/入库失败删除本次对象。
     - `deleteFlightFileByJobId`：删除记录与对应的 MinIO 对象（一个任务一份）。
     - 事务边界：下载/解压/上传/回读都在事务外，只有 `insert`、`delete` 用 `TransactionTemplate` 单独开事务。
4. 任务删除：`WaylineJobServiceImpl#deleteJob` 在删除 `wayline_job` 后联动 `deleteFlightFileByJobId`；原始航线文件不删除。
5. 下发链路：`FlightTaskServiceImpl.prepareFlightTask` → `getFlightTaskFile(...)`
   - `exit_wayline_when_rc_lost` 为空：沿用原始文件 URL + 原始 MD5（保持旧行为）。
   - 非空：用副本 `object_key` 取预签名 URL，`fingerprint` 用副本 MD5。
   - 蛙跳 `prepareMultiDockFlightTask` 同样调用 `getFlightTaskFile`，两个机场下发同一份副本。
6. 常量：`KmzFileProperties` 维护 `missionConfig` 节点名、动作取值、副本目录 `wayline_file_copy`。

## 本地验证（不连 MinIO / 数据库）

入口：`sample/src/test/java/com/dji/sample/wayline/util/KmzFileUtilsLocalTest#main`

- 默认读 `SOURCE_KMZ` 写到 `DEST_DIR`，也可传参：`args[0]` 原 KMZ、`args[1]` 输出目录、`args[2]` `exit_wayline_when_rc_lost`、`args[3]` `out_of_control`。
- 输出 `{原文件名}_{时间戳}.kmz`，解压后核对 `wpmz/template.kml`、`wpmz/waylines.wpml` 的 `wpml:exitOnRCLost`、`wpml:executeRCLostAction`。
- 改写逻辑在 `com.dji.sample.wayline.util.KmzFileUtils`（纯内存函数），线上服务与本地测试共用同一份代码，不存在两份实现。

## 自测要点

- 立即任务：`taskType=IMMEDIATE`，`exitWaylineWhenRcLost=1`、`outOfControlAction=0/1/2`，
  检查 `wayline_file_flight` 是否新增一行，MinIO 中 `.../wayline_file_copy/xxx_{时间戳}.kmz` 是否存在。
- 下载副本解压后确认 `wpmz/template.kml` 与 `wpmz/waylines.wpml` 的 `wpml:exitOnRCLost`、
  `wpml:executeRCLostAction` 已按映射表改写，且 MD5 与 `wayline_file_flight.sign` 一致。
- 定时/条件任务：`prepareConditionJob`、`retryPrepareJob` 重复准备时不应产生多份副本（`job_id` 复用）。
- 回归：`exitWaylineWhenRcLost` 为空时，下发报文中的 `url`/`fingerprint` 与改造前完全一致。
- 删除任务：调用删除飞行计划接口后，`wayline_file_flight` 记录与 MinIO 中 `wayline_file_copy/*.kmz` 均消失，而 `wayline_file` 与其原始对象仍在。
- 上传失败：模拟 MinIO 不可写（错误桶/断网）时，任务准备失败且副本表无残留记录、桶内无残留对象。
- 非标准 KMZ：把 `wpmz/` 包一层目录后上传，任务应能正常准备（日志出现 “The wayline file has no wpmz/template.kml or wpmz/waylines.wpml”），副本内容与原文件一致。

## 排错

- 报文准备失败且日志出现“航线文件解压失败”：确认上传的是标准 KMZ（zip 结构正常、UTF-8 编码）。
- 日志出现 “The wayline file has no wpmz/template.kml or wpmz/waylines.wpml”：KMZ 结构非标准，已降级为原文件下发，失联动作不会生效，需核对压缩包内条目路径。
- “文件名已存在”：对象 key 冲突，检查时间戳生成逻辑是否被清理掉。
- 上传无异常但文件不存在：`IOssService#putObject` 内部吞异常，现在在入库前会回读校验长度，校验失败会抛“航线文件副本上传失败”，需查 MinIO 连接/桶权限日志。
