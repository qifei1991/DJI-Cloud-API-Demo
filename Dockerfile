# 双架构镜像构建说明（配合 .drone.yml 的 CICD_cloud-service-multiarch 流水线）：
#   基础镜像通过 --build-arg BASE_IMAGE 注入，amd64 / arm64 各用一个基础镜像：
#     amd64: 10.43.11.21:5000/openjdk:11.0.14.1-jdk-buster
#     arm64: 10.43.11.21:5000/openjdk:11.0.14.1-arm-jdk-buster
#
#   ！！本文件禁止出现 RUN 指令：runner 未安装 QEMU/binfmt，跨架构 build 执行不了目标架构二进制。
#      一旦加回 RUN，必须先在 runner 上执行：docker run --privileged --rm tonistiigi/binfmt --install arm64
#      JDK11 的时区用 ENV TZ=Asia/Shanghai 即可（JVM 直接读取），不能再用 RUN ln -sf 改 /etc/localtime。
#
# ！！本文件 ENTRYPOINT 用 "\" 续行（多行 JSON 数组），依赖 .gitattributes 强制 Dockerfile 以 LF 检出：
#   仓库 core.autocrlf=true，若无 .gitattributes，Windows 检出为 CRLF，"\" 后跟 CRLF 会破坏解析（构建静默损坏）。
#   不要删除 .gitattributes 中的对应行。
#
# 镜像内目录结构（部署时把宿主配置目录挂载到 /app/config 即可外部改配置）：
#   /app/cloud-service.jar       运行 jar（由 CI 打包后 COPY 进来；sample/pom.xml finalName 固定名）
#   /app/config/                 外置配置目录（镜像内不预建、不内置 application.yml，挂载时由 Docker
#                                自动创建挂载点，配置完全由挂载的宿主目录提供，避免与挂载配置冲突导致参数失效。
#                                CI 打包 -Penv-prod 激活 profile 把 application*.yml 排除出 jar，
#                                Spring Boot 默认扫描 jar 同级的 ./config/，可自动加载挂载进来的配置）
#   /app/logs                    GC 日志（/app/logs/gc.log）与 OOM 堆转储目录，运行时需挂载宿主目录到 /app/logs
ARG BASE_IMAGE=10.43.11.21:5000/openjdk:11.0.14.1-jdk-buster
FROM ${BASE_IMAGE}

ENV TZ=Asia/Shanghai
ENV LANG=zh_CN.utf8

# 核数与堆内存做成环境变量，k8s / docker 部署时与 limits 对应（组织模式参考 aircraft-manager 的 Dockerfile，
#   该文件面向 JDK8，参数不能照抄，以下均为 JDK11 写法）：
#   CORE → -XX:ActiveProcessorCount，与 cpu limit 一致；MEM → -Xms/-Xmx（等值避免堆伸缩抖动）。
#   小内存宿主用 -e MEM=2g -e CORE=2 等覆盖，不必重建镜像。
# ！！内存预算：容器内存限额 ≥ 1.5×MEM（元空间/代码缓存/线程栈为堆外开销；未显式设 MaxDirectMemorySize，
#   默认值≈MEM，MQTT 客户端等会占直接内存），不足时可能被 cgroup OOM Kill。
ENV CORE=4
ENV MEM=4g
# 追加 JVM 参数：拼在 ENTRYPOINT 固定参数（"$@"）之后、-jar 之前，同名 -X/-XX 选项后生效可覆盖默认值
#   （如 -e JAVA_OPTS="-XX:MaxGCPauseMillis=100"）。
# ！！JDK11 不能经此变量切换为 JDK8 的 CMS 系列收集器：UseParNewGC 等在 JDK9+ 已移除，
#   会报 Unrecognized VM option 直接启动失败；换收集器必须改下方 ENTRYPOINT。
ENV JAVA_OPTS=""

WORKDIR /app

COPY sample/target/cloud-service.jar /app/

# JVM 参数分两组（结构参考 aircraft-manager 的 Dockerfile，参数按 JDK11 调整）：
#   可变参数（MEM/CORE/JAVA_OPTS）写在 sh -c 模板里由 sh 展开（exec 形式不做环境变量展开）；
#   固定参数拆成独立数组元素，作为第 4 个元素 "sh"（$0）之后的位置参数经 "$@" 原样透传。
#   ${JAVA_OPTS} 在 "$@" 之后、-jar 之前：同名选项后生效保留追加覆盖语义（-jar 之后的选项会被 java 当作应用参数）。
# JDK11 与 JDK8 参数差异（只借鉴组织模式，参数不混用 JDK8 写法，否则启动失败或告警）：
#   1. 收集器：JDK9+ 默认 G1，-XX:+UseG1GC 仅作显式声明；不写 NewRatio/SurvivorRatio 等
#      新生代参数（干扰 G1 Region 自适应）；
#   2. GC 日志：统一日志 -Xlog:gc* 自带轮转（filecount/filesize），替代 JDK8 的 -Xloggc + PrintGC* 系列
#      （PrintGCDetails/PrintGCTimeStamps 在 JDK9+ 已废弃）；
#   3. 容器感知：UseContainerSupport 在 JDK11 默认开启，无需书写，ActiveProcessorCount 直接生效。
# GC 日志与堆 dump 用绝对路径 /app/logs，运行时必须把宿主机 logs 目录挂载到 /app/logs，否则容器销毁即丢失。
# exec 形式 + "\" 续行：解析器按行切分指令，多行 JSON 数组必须靠行尾 "\" 合并（无续行时每行被当作独立指令，
#   构建静默损坏）；续行中的注释行会被解析器剥离，因此参数间可插分组注释。
# exec 启动 java 使其成为 PID 1：docker stop 的 SIGTERM 能直达 JVM，不会出现 sh 吞信号导致 JVM 被 SIGKILL。
# jar 名固定 cloud-service.jar（sample/pom.xml finalName），运行时不再 ls 找 jar；
# 缺失时 java 报 Unable to access jarfile 并非零退出，不会空转成“假活着”；交付前必须 mvn clean package。
ENTRYPOINT ["sh", "-c", "exec java -Xms1g -Xmx${MEM} -XX:ActiveProcessorCount=${CORE} \"$@\" ${JAVA_OPTS} -jar cloud-service.jar", "sh", \
  # G1 收集器与停顿目标（JDK9+ 默认 G1，显式声明便于阅读；替换收集器必须改此 ENTRYPOINT）
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=200", \
  # 元空间：初始高水位触发阈值（不设 MaxMetaspaceSize，由容器内存限额兜底）
  "-XX:MetaspaceSize=256m", \
  # 禁用 System.gc()（纯 Java 服务无堆外 Direct Buffer 大户）
  "-XX:+DisableExplicitGC", \
  # OOM：先 dump 堆到 /app/logs 再立即退出，交容器重启策略拉起（dump 与 ExitOnOOM 可共存）
  "-XX:+HeapDumpOnOutOfMemoryError", \
  "-XX:HeapDumpPath=/app/logs/", \
  "-XX:+ExitOnOutOfMemoryError", \
  # GC 日志：统一日志带时间戳/存活时长/级别/标签，轮转 5×20M（JDK9+ 写法，替代 JDK8 的 -Xloggc 系列）
  "-Xlog:gc*:file=/app/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=20m", \
  # 系统属性：编码/时区/无头模式/熵源（urandom 避免 SecureRandom 阻塞）
  "-Dfile.encoding=UTF-8", \
  "-Duser.timezone=Asia/Shanghai", \
  "-Djava.awt.headless=true", \
  "-Djava.security.egd=file:/dev/./urandom"]
