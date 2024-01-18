package com.venticonsulting.waapi.entity.waapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@Getter
@ToString
@NoArgsConstructor
public class SendMediaFileRequest {

    @JsonProperty("mediaUrl")
    private String mediaUrl;
    @JsonProperty("chatId")
    private String chatId;
    @JsonProperty("mediaCaption")
    private String mediaCaption;
    @JsonProperty("mediaName")
    private String mediaName;

}
