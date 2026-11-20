package com.dji.sample.wayline.util;

import com.dji.sdk.cloudapi.wayline.OutOfControlActionEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 本地静态测试：不连接 MinIO、不依赖数据库，直接读取本地 KMZ 文件，
 * 按飞行计划的失联参数改写 missionConfig 节点后，在本地生成一份 KMZ 副本，便于人工解压核对。
 *
 * 使用方式：
 * 1. 修改下面的 SOURCE_KMZ / DEST_DIR，或直接传参运行：
 *    args[0] 原始 KMZ 路径，args[1] 副本输出目录，args[2] exit_wayline_when_rc_lost，args[3] out_of_control
 * 2. 运行 main 方法，副本文件名为「原文件名_时间戳.kmz」。
 *
 * @author sean
 * @version 1.0
 * @date 2026/11/20
 */
public class KmzFileUtilsLocalTest {

    /** 原始 KMZ 文件路径，按需修改 */
    private static final String SOURCE_KMZ = "D:\\Users\\Administrator\\Downloads\\新建面状航线1794207540701.kmz";

    /** 副本输出目录，按需修改 */
    private static final String DEST_DIR = "D:\\Users\\Administrator\\Downloads\\copy";

    /** wayline_job.exit_wayline_when_rc_lost：0 继续执行航线，1 退出航线并执行失联动作 */
    private static final Integer EXIT_WAYLINE_WHEN_RC_LOST = 0;

    /** wayline_job.out_of_control：0 返航，1 悬停，2 降落 */
    private static final Integer OUT_OF_CONTROL_ACTION = 0;

    public static void main(String[] args) throws Exception {
        String sourceKmz = args.length > 0 ? args[0] : SOURCE_KMZ;
        String destDir = args.length > 1 ? args[1] : DEST_DIR;
        Integer exitWaylineWhenRcLost = args.length > 2
                ? Integer.valueOf(args[2]) : EXIT_WAYLINE_WHEN_RC_LOST;
        Integer outOfControlAction = args.length > 3
                ? Integer.valueOf(args[3]) : OUT_OF_CONTROL_ACTION;

        Path copyPath = modifyLocalKmz(sourceKmz, destDir, exitWaylineWhenRcLost, outOfControlAction);

        System.out.println("原始航线文件: " + Paths.get(sourceKmz).toAbsolutePath());
        System.out.println("副本已生成  : " + copyPath.toAbsolutePath());
        System.out.println("解压副本后重点查看: wpmz/template.kml 与 wpmz/waylines.wpml 中的 "
                + "wpml:exitOnRCLost、wpml:executeRCLostAction 节点。");
    }

    /**
     * 读取本地 KMZ → 改写失联动作 → 在本地生成副本文件（不上传、不入库）。
     * @param sourceKmz 原始 KMZ 路径
     * @param destDir 副本输出目录
     * @param exitWaylineWhenRcLost wayline_job.exit_wayline_when_rc_lost，为空时按 0 处理
     * @param outOfControlAction wayline_job.out_of_control，为空时不改写 executeRCLostAction
     * @return 副本文件路径
     */
    public static Path modifyLocalKmz(String sourceKmz, String destDir,
            Integer exitWaylineWhenRcLost, Integer outOfControlAction) throws IOException, SQLException {
        Path sourcePath = Paths.get(sourceKmz);
        if (!Files.isRegularFile(sourcePath)) {
            throw new IllegalArgumentException("原始 KMZ 文件不存在: " + sourcePath.toAbsolutePath());
        }

        String exitOnRcLost = KmzFileUtils.getExitOnRcLost(exitWaylineWhenRcLost);
        OutOfControlActionEnum actionEnum = Objects.isNull(outOfControlAction)
                ? null : OutOfControlActionEnum.find(outOfControlAction);
        String executeRcLostAction = KmzFileUtils.getExecuteRcLostAction(actionEnum);

        byte[] copyContent = KmzFileUtils.modifyRcLostAction(
                Files.readAllBytes(sourcePath), exitOnRcLost, executeRcLostAction);

        Path destPath = Files.createDirectories(Paths.get(destDir))
                .resolve(buildCopyName(sourcePath.getFileName().toString()));
        Files.write(destPath, copyContent);

        System.out.printf("参数映射: exitOnRCLost=%s, executeRCLostAction=%s%n",
                exitOnRcLost, executeRcLostAction);
        return destPath;
    }

    private static String buildCopyName(String fileName) {
        int index = fileName.toLowerCase().lastIndexOf(".kmz");
        String name = index > 0 ? fileName.substring(0, index) : fileName;
        return name + "_" + System.currentTimeMillis() + ".kmz";
    }
}
