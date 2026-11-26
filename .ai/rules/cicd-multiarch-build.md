# CICD 双架构镜像构建规范（cloud-service / DJI Cloud API Demo）

## 适用范围
- 服务：cloud-service（本仓库，Spring Boot 2.7 + JDK11，Maven 多模块：cloud-sdk + sample）
- 流水线：`.drone.yml`（CICD_cloud-service-multiarch），触发方式：tag / tag_push
- 产物：amd64 与 arm64 单架构镜像 + 合并后的 manifest list（均为带版本号的 tag，不发布 :latest）

## 关键约定与陷阱（修改前必读）
1. **纯 Java 服务只构建一次 Maven**：jar 与 CPU 架构无关，两个镜像构建步骤共用
   `sample/target/cloud-service.jar`，可并行。与 aircraft-calculate（内含平台相关 .so，需按平台
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
7. **配置外置约定**：CI 打包命令必须带 `-Penv-prod`（或 `-Denv=prod`）激活 sample/pom.xml 的
   env-prod profile，把 `application*.yml` 排除出 jar（CI 不对 jar 内做 yml 检查，
   若因漏加参数打入则不做处理）；镜像内不 COPY
   `application.yml`、也不预建 `/app/config/` 目录（挂载时由 Docker 自动创建挂载点，
   与 `/app/logs` 同理），避免与挂载配置冲突导致参数失效。配置完全由部署时
   挂载到 `/app/config` 的宿主目录提供（Spring Boot 默认扫描 jar 同级 `./config/`），
   改配置无需重打镜像。本地开发不激活 profile（yml 进 jar，便于 IDE 直接跑），与 CI 的差异
   仅由 -Penv-prod 决定；pom 不设 os 自动激活的 profile——避免双 profile 同时激活时
   resources 合并导致 excludes 失效（已实测踩坑）。
8. **镜像 tag 约定**：只发布带版本号的 tag。无后缀版本 tag（如 1.10.42）为 manifest list，
   在线 pull 按宿主架构自动选层，部署脚本无需区分架构；`-amd64` / `-arm64` 后缀为单架构
   镜像（离线 docker save/load 交付用，manifest list 不支持 save）。不发布 :latest
   （含单架构 latest 后缀 tag）：避免 latest 漂移导致各环境拉到非预期版本，部署固定用
   版本号 tag；部署脚本如引用旧 :latest 需改为具体版本。
9. **jar 命名**：sample/pom.xml 用 `<finalName>${project.artifactId}</finalName>` 去掉默认的
   -${revision} 后缀，artifactId 为 cloud-service，产物即固定名 `sample/target/cloud-service.jar`，
   Dockerfile 的 COPY 与 ENTRYPOINT 均用固定名，运行时不再 ls 找 jar；jar 缺失时 java 报
   Unable to access jarfile 并非零退出（不会空转）。repackage 的原始包是
   `cloud-service.jar.original`，精确文件名 COPY 不会误匹配；CI 打包步骤有固定名产物
   存在性断言，交付前必须 mvn clean package；改名需同步 Dockerfile / .drone.yml /
   .dockerignore 三处。
10. **镜像仓库地址**：`10.43.11.21:5000/dlwy/mapzone-uav/cloud-service`（固定名，不用
    $DRONE_REPO_NAME，与服务名/jar 名统一）。
11. **JVM 参数分环境变量 + ENTRYPOINT 固定参数两组，且 JDK11 不能照抄 JDK8 参数**（组织模式
    参考 aircraft-manager 的 Dockerfile，该文件面向 JDK8，参数不能照抄）：
    - 可变参数用 ENV：CORE（→ -XX:ActiveProcessorCount，与 cpu limit 对应）、MEM（→ -Xms/-Xmx
      等值，避免堆伸缩抖动）、JAVA_OPTS（追加覆盖参数），运行时 `docker run -e MEM=2g ...`
      覆盖，不必重建镜像；固定参数拆成 ENTRYPOINT JSON 数组元素（"$@" 透传），${JAVA_OPTS}
      拼在其后、-jar 之前，同名选项后生效可覆盖默认值；
    - ENTRYPOINT 用 exec 形式 + "\" 续行，续行间可插注释行（解析器会剥离）；该写法依赖
      `.gitattributes` 强制 Dockerfile 以 LF 检出——仓库 core.autocrlf=true，无 .gitattributes 时
      Windows 检出为 CRLF，"\" 后跟 CRLF 会破坏解析（构建静默损坏），不要删除对应规则行；
    - JDK8 的 CMS 系列参数（UseParNewGC/UseConcMarkSweepGC/CMSInitiatingOccupancyFraction 等）
      在 JDK9+ 已移除，会报 Unrecognized VM option 启动失败，也不能经 JAVA_OPTS 切换为 CMS；
      G1（JDK9+ 默认）下不要手工指定 NewSize/SurvivorRatio 等新生代参数，会干扰 Region 自适应；
      换收集器必须改 ENTRYPOINT；
    - JDK8 的 -XX:+PrintGCDetails/-XX:+PrintGCTimeStamps/-Xloggc 在 JDK9+ 已废弃，
      用统一日志 `-Xlog:gc*:file=/app/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=20m`
      （自带轮转）；
    - -XX:HeapDumpPath 与 gc 日志用绝对路径 /app/logs，运行时需挂载宿主目录到 /app/logs，
      否则堆转储/GC 日志写进容器可写层，容器删除即丢失；
    - UseContainerSupport 在 JDK11 默认开启，无需显式书写，ActiveProcessorCount 直接生效。

## 相关文件
- 流水线定义：`.drone.yml`
- 镜像构建：`Dockerfile`
- 构建入口：根 `pom.xml`、`sample/pom.xml`、`cloud-sdk/pom.xml`
