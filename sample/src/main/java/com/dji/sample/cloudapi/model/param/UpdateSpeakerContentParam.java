package com.dji.sample.cloudapi.model.param;

import com.dji.sample.psdk.model.enums.SpeakerContentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Qfei
 * @date 2025/6/25 18:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UpdateSpeakerContentParam {

    @NotNull
    private SpeakerContentTypeEnum type;

    @NotBlank
    private String contentId;

    /**
     * 喊话记录名称
     */
    private String name;

    /**
     * TTS 内容
     */
    private String content;

    /**
     * username
     */
    private String username = "manager-server";

    /**
     * 组织编码
     */
    private String code;

    @Override
    public String toString() {
        return "UpdateSpeakerContentParam{" +
                "type=" + type +
                ", contentId='" + contentId + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", username='" + username + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
