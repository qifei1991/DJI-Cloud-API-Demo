# 双架构镜像构建说明（配合 .drone.yml 的 CICD_cloud-service-multiarch 流水线）：
#   基础镜像通过 --build-arg BASE_IMAGE 注入，amd64 / arm64 各用一个基础镜像：
#     amd64: 10.43.11.21:5000/openjdk:11.0.14.1-jdk-buster
#     arm64: 10.43.11.21:5000/openjdk:11.0.14.1-arm-jdk-buster
#
#   ！！本文件禁止出现 RUN 指令：runner 未安装 QEMU/binfmt，跨架构 build 执行不了目标架构二进制。
#      一旦加回 RUN，必须先在 runner 上执行：docker run --privileged --rm tonistiigi/binfmt --install arm64
#      JDK11 的时区用 ENV TZ=Asia/Shanghai 即可（JVM 直接读取），不能再用 RUN ln -sf 改 /etc/localtime。
#
# 镜像内目录结构（部署时把宿主配置目录挂载到 /app/config 即可外部改配置）：
#   /app/sample-*.jar            运行 jar（由 CI 打包后 COPY 进来）
#   /app/config/application.yml  外置配置。sample 的 pom 已把 application*.yml 排除出 jar，
#                                Spring Boot 默认扫描 jar 同级的 ./config/，可自动加载到该文件
#   /app/logs                    GC 日志（logs/gc.log）与 OOM 堆转储目录，运行时需挂载宿主目录到 /app/logs
ARG BASE_IMAGE=10.43.11.21:5000/openjdk:11.0.14.1-jdk-buster
FROM ${BASE_IMAGE}

ENV TZ=Asia/Shanghai
ENV LANG=zh_CN.utf8

# JVM 参数（参考 aircraft-calculate 的 Dockerfile，已按 JDK11 调整）。
# 做成 ENV 是为了能用 docker run -e JAVA_OPTS=... 覆盖，不必重建镜像。
# ！！-Xlog 的 gc 文件与 -XX:HeapDumpPath 指向相对目录 logs（相对 WORKDIR /app），
#    运行时必须把宿主机 logs 目录挂载到 /app/logs，否则日志/堆转储会写进容器可写层，容器删了就丢。
# 相对 JDK8 原参数的调整（不调整的话 JVM 启动失败或日志参数无效）：
#   1. 删除 -XX:+UseParNewGC / -XX:+UseConcMarkSweepGC 及 CMS 系列参数：
#      JDK9 起默认 G1，CMS 在 JDK11 中手工开启会打告警（JDK14 已彻底移除）；
#   2. 删除 -XX:NewSize/-XX:MaxNewSize/-XX:NewRatio/-XX:SurvivorRatio/-XX:MaxTenuringThreshold：
#      G1 自适应管理分代/Region，手工指定新生代会干扰其调优；
#   3. -XX:+PrintGCDetails/-XX:+PrintGCTimeStamps/-Xloggc 是 JDK8 打印参数，JDK9 起废弃：
#      改用统一日志 -Xlog:gc*:file=logs/gc.log:...，自带轮转（filecount/filesize），无需手工清理；
#   4. 保留：堆内存配置 / -XX:MetaspaceSize / -XX:+DisableExplicitGC /
#      -XX:+HeapDumpOnOutOfMemoryError / -XX:HeapDumpPath（OOM 时自动留堆转储）。
ENV JAVA_OPTS="-Dfile.encoding=utf-8 -Duser.timezone=GMT+8 -Xms1g -Xmx4g -XX:MetaspaceSize=256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs -Xlog:gc*:file=logs/gc.log:time,uptime,level,tags:filecount=5,filesize=20m"

WORKDIR /app

COPY sample/target/sample-*.jar /app/
COPY sample/src/main/resources/application.yml /app/config/application.yml

# exec 启动 java 使其成为 PID 1：docker stop 的 SIGTERM 能直达 JVM，不会出现 sh 吞信号导致 JVM 被 SIGKILL。
# jar 取 ls -t 的第一个；找不到 jar 时报错退出（非 0），避免容器空转成“假活着”。
ENTRYPOINT ["sh", "-c", "JAR_FILE=$(ls -t /app/sample-*.jar 2>/dev/null | head -n 1); if [ -z \"$JAR_FILE\" ]; then echo \"ERROR: no sample-*.jar in /app, check CI build step\"; exit 1; fi; echo \"Starting run jar: $JAR_FILE\"; exec java $JAVA_OPTS -jar \"$JAR_FILE\""]
