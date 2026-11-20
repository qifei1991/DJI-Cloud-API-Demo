# CICD 双架构镜像构建规范（cloud-service / DJI Cloud API Demo）

## 适用范围
- 服务：cloud-service（本仓库，Spring Boot 2.7 + JDK11，Maven 多模块：cloud-sdk + sample）
- 流水线：`.drone.yml`（CICD_cloud-service-multiarch），触发方式：tag / tag_push
- 产物：amd64 与 arm64 单架构镜像 + 合并后的 manifest list（无后缀 tag 与 :latest）

## 关键约定与陷阱（修改前必读）
1. **纯 Java 服务只构建一次 Maven**：jar 与 CPU 架构无关，两个镜像构建步骤共用
   `sample/target/sample-*.jar`，可并行。与 aircraft-calculate（内含平台相关 .so，需按平台
   两次 `-Dplatform` 打包且严格串行）不同，不要再按平台打两次包。
2. **Dockerfile 禁止 RUN 指令**：runner 未装 QEMU/binfmt，跨架构 build 执行不了目标架构
   二进制，含 RUN 的 Dockerfile 会在 arm64 构建时失败。时区用 `ENV TZ=Asia/Shanghai`
   （JVM 直接读取），不要用 `RUN ln -sf` 改 /etc/localtime。如必须加 RUN，先在 runner 执行：
   `docker run --privileged --rm tonistiigi/binfmt --install arm64`
3. **基础镜像注入**：Dockerfile 用 `ARG BASE_IMAGE` + `FROM ${BASE_IMAGE}`，CI 通过
   `--build-arg BASE_IMAGE` 注入：
   - amd64：`10.43.11.21:5000/openjdk:11.0.14.1-jdk-buster`
   - arm64：`10.43.11.21:5000/openjdk:11.0.14.1-arm-jdk-buster`
4. **--platform 必须走 BuildKit**：镜像构建步骤内设 `DOCKER_BUILDKIT=1`，否则经典 builder
   报 "requires BuildKit"。
5. **http 私仓的 manifest 命令必须带 --insecure**：`docker manifest` 是 CLI 直连 registry，
   不读 daemon.json 的 insecure-registries；漏掉会报
   "http: server gave HTTP response to HTTPS client"。
6. **构建后断言产物架构**：`docker image inspect --format '{{.Architecture}}'`，防止基础镜像
   被标错架构导致 manifest list 出现两个同架构条目。
7. **配置外置约定**：sample 的 pom 已把 `application*.yml` 排除出 jar；镜像内配置固定放
   `/app/config/application.yml`（Spring Boot 默认扫描 jar 同级 `./config/`）。部署时把宿主
   配置目录挂载到 `/app/config` 即可改配置，无需重打镜像。
8. **镜像 tag 约定**：`-amd64` / `-arm64` 后缀为单架构镜像（离线 docker save/load 交付用，
   manifest list 不支持 save）；无后缀 tag 与 `:latest` 为 manifest list，在线 pull 按宿主
   架构自动选层，部署脚本无需区分架构。
9. **jar 命名**：产物为 `sample-<revision>.jar`（revision 在根 pom 定义），Dockerfile 用
   `sample-*.jar` 通配 + `ls -t | head -n 1` 选取，repackage 的原始包是 `*.jar.original`，
   不会被通配误匹配。
10. **镜像仓库地址**：`10.43.11.21:5000/dlwy/mapzone-uav/$DRONE_REPO_NAME`（$DRONE_REPO_NAME
    为 Drone 中的仓库名）。
11. **JVM 启动参数用 ENV JAVA_OPTS，且 JDK11 不能照抄 JDK8 参数**：
    - 参数放 ENV JAVA_OPTS，运行时用 `docker run -e JAVA_OPTS=...` 覆盖，不必重建镜像；
    - JDK8 的 CMS 系列参数（UseParNewGC/UseConcMarkSweepGC/CMSInitiatingOccupancyFraction 等）
      在 JDK11 下会告警甚至失败，G1（JDK9+ 默认）下不要手工指定 NewSize/SurvivorRatio 等
      新生代参数，会干扰 Region 自适应；
    - JDK8 的 -XX:+PrintGCDetails/-XX:+PrintGCTimeStamps/-Xloggc 在 JDK9+ 已废弃，
      用统一日志 `-Xlog:gc*:file=logs/gc.log:time,uptime,level,tags:filecount=5,filesize=20m`
      （自带轮转）；
    - -XX:HeapDumpPath 与 gc 日志指向相对目录 logs，运行时需挂载宿主目录到 /app/logs，
      否则堆转储/GC 日志写进容器可写层，容器删除即丢失。

## 相关文件
- 流水线定义：`.drone.yml`
- 镜像构建：`Dockerfile`
- 构建入口：根 `pom.xml`、`sample/pom.xml`、`cloud-sdk/pom.xml`
