package com.dji.sample.wayline.util;

import com.dji.sample.wayline.model.dto.KmzFileProperties;
import com.dji.sdk.cloudapi.device.ExitWaylineWhenRcLostEnum;
import com.dji.sdk.cloudapi.wayline.OutOfControlActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * The helper of the wayline kmz file.
 * The lost action in the missionConfig node is modified without any object storage and database,
 * so the kmz file can be modified and checked locally.
 * @author sean
 * @version 1.0
 * @date 2026/11/20
 */
@Slf4j
public class KmzFileUtils {

    private KmzFileUtils() {
    }

    /**
     * Convert the value of the wayline_job.exit_wayline_when_rc_lost field into the value of the wpml node.
     * Only 1 means exiting the wayline and executing the rc lost action, the others keep the wayline.
     * @param exitWaylineWhenRcLost
     * @return
     */
    public static String getExitOnRcLost(Integer exitWaylineWhenRcLost) {
        return Objects.nonNull(exitWaylineWhenRcLost)
                && ExitWaylineWhenRcLostEnum.EXECUTE_RC_LOST_ACTION.getAction() == exitWaylineWhenRcLost
                ? KmzFileProperties.EXIT_ON_RC_LOST_EXECUTE_ACTION
                : KmzFileProperties.EXIT_ON_RC_LOST_CONTINUE;
    }

    /**
     * Convert the value of the wayline_job.out_of_control field into the value of the wpml node.
     * @param outOfControlAction
     * @return null means the lost action in the wayline file does not need to be modified.
     */
    public static String getExecuteRcLostAction(OutOfControlActionEnum outOfControlAction) {
        if (Objects.isNull(outOfControlAction)) {
            return null;
        }
        switch (outOfControlAction) {
            case HOVERING:
                return KmzFileProperties.RC_LOST_ACTION_HOVER;
            case LANDING:
                return KmzFileProperties.RC_LOST_ACTION_LANDING;
            case RETURN_TO_HOME:
            default:
                return KmzFileProperties.RC_LOST_ACTION_GO_BACK;
        }
    }

    /**
     * Unzip the kmz file, modify the lost action of the wpml file and compress it again.
     * @param content the content of the kmz file
     * @param exitOnRcLost
     * @param executeRcLostAction
     * @return
     */
    public static byte[] modifyRcLostAction(byte[] content, String exitOnRcLost, String executeRcLostAction) {
        Map<String, byte[]> entries = unzip(content);

        List<String> waylineXmlNames = entries.keySet().stream()
                .filter(KmzFileUtils::isWaylineXml)
                .collect(Collectors.toList());
        if (waylineXmlNames.isEmpty()) {
            // The structure of the wayline file is not standard, keep the original content to avoid failing the task.
            log.warn("The wayline file has no {}/{} or {}/{}, the lost action is not modified.",
                    KmzFileProperties.FILE_DIR_FIRST, KmzFileProperties.FILE_DIR_SECOND_TEMPLATE,
                    KmzFileProperties.FILE_DIR_FIRST, KmzFileProperties.FILE_DIR_SECOND_WAYLINES);
            return content;
        }
        for (String name : waylineXmlNames) {
            entries.put(name, modifyWaylineXml(entries.get(name), exitOnRcLost, executeRcLostAction));
        }
        return zip(entries);
    }

    private static boolean isWaylineXml(String entryName) {
        return (KmzFileProperties.FILE_DIR_FIRST + "/" + KmzFileProperties.FILE_DIR_SECOND_TEMPLATE).equals(entryName)
                || (KmzFileProperties.FILE_DIR_FIRST + "/" + KmzFileProperties.FILE_DIR_SECOND_WAYLINES).equals(entryName);
    }

    private static Map<String, byte[]> unzip(byte[] content) {
        Map<String, byte[]> entries = new LinkedHashMap<>(16);
        try (ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
             ZipInputStream unzipFile = new ZipInputStream(contentStream, StandardCharsets.UTF_8)) {
            ZipEntry nextEntry = unzipFile.getNextEntry();
            while (Objects.nonNull(nextEntry)) {
                entries.put(nextEntry.getName(), unzipFile.readAllBytes());
                nextEntry = unzipFile.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("航线文件解压失败。", e);
        }
        if (entries.isEmpty()) {
            throw new RuntimeException("航线文件内容为空。");
        }
        return entries;
    }

    private static byte[] zip(Map<String, byte[]> entries) {
        try (ByteArrayOutputStream zipContent = new ByteArrayOutputStream();
             ZipOutputStream zipFile = new ZipOutputStream(zipContent, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                zipFile.putNextEntry(new ZipEntry(entry.getKey()));
                zipFile.write(entry.getValue());
                zipFile.closeEntry();
            }
            zipFile.finish();
            return zipContent.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("航线文件压缩失败。", e);
        }
    }

    /**
     * Modify the wpml:exitOnRCLost and wpml:executeRCLostAction node in the missionConfig node.
     * @param content the content of the xml file
     * @param exitOnRcLost
     * @param executeRcLostAction skip the modification when it is null.
     * @return
     */
    private static byte[] modifyWaylineXml(byte[] content, String exitOnRcLost, String executeRcLostAction) {
        try {
            SAXReader reader = new SAXReader();
            // The wayline file is uploaded by the user, so the doctype declaration and the external entities are disabled.
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Document document = reader.read(new ByteArrayInputStream(content));
            Node missionConfig = document.selectSingleNode(
                    "//*[local-name()='" + KmzFileProperties.TAG_MISSION_CONFIG + "']");
            if (Objects.isNull(missionConfig)) {
                log.warn("The wayline file has no missionConfig node, skip modifying the lost action.");
                return content;
            }
            setNodeText((Element) missionConfig, KmzFileProperties.TAG_EXIT_ON_RC_LOST, exitOnRcLost);
            if (StringUtils.hasText(executeRcLostAction)) {
                setNodeText((Element) missionConfig, KmzFileProperties.TAG_EXECUTE_RC_LOST_ACTION, executeRcLostAction);
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(StandardCharsets.UTF_8.name());
            // Avoid the extra blank line after the xml declaration.
            format.setNewLineAfterDeclaration(false);
            ByteArrayOutputStream xmlContent = new ByteArrayOutputStream();
            XMLWriter writer = new XMLWriter(xmlContent, format);
            writer.write(document);
            writer.close();
            return xmlContent.toByteArray();
        } catch (DocumentException | IOException | SAXException e) {
            throw new RuntimeException("修改航线文件失联动作失败。", e);
        }
    }

    private static void setNodeText(Element parent, String tagName, String value) {
        Node node = parent.selectSingleNode("*[local-name()='" + tagName + "']");
        if (Objects.nonNull(node)) {
            node.setText(value);
            return;
        }
        Namespace namespace = getChildNamespace(parent);
        Element element = Objects.isNull(namespace)
                ? parent.addElement(tagName) : parent.addElement(QName.get(tagName, namespace));
        element.setText(value);
    }

    private static Namespace getChildNamespace(Element parent) {
        for (Element child : parent.elements()) {
            Namespace namespace = child.getNamespace();
            if (Objects.nonNull(namespace) && StringUtils.hasText(namespace.getURI())) {
                return namespace;
            }
        }
        return null;
    }
}
