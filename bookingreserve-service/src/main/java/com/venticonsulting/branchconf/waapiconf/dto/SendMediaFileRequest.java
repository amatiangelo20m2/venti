package com.venticonsulting.branchconf.waapiconf.dto;

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
