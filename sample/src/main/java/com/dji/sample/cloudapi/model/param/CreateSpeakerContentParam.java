package com.dji.sample.cloudapi.model.param;

import com.dji.sample.psdk.model.enums.SpeakerContentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author Qfei
 * @date 2025/6/25 18:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CreateSpeakerContentParam {

    @NotNull
    private SpeakerContentTypeEnum type;

    private String creator = "manager-server";

    private String code;

    private String name;

    private String content;

    @Override
    public String toString() {
        return "CreateSpeakerContentParam{" +
                "creator='" + creator + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
