package com.venticonsulting.branchconf.waapiconf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfilePicResponse implements Serializable {

    @JsonProperty("data")
    private Data data;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("status")
    private String status;

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Data {
        @JsonProperty("status")
        private String status;

        @JsonProperty("instanceId")
        private String instanceId;

        @JsonProperty("data")
        private ProfileData profileData;

    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ProfileData {
        @JsonProperty("profilePicUrl")
        private String profilePicUrl;

    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Links {
        @JsonProperty("self")
        private String self;

    }
}

