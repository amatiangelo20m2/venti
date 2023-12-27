package com.venticonsulting.waapi.entity.waapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@Getter
@ToString
@NoArgsConstructor
public class MeResponse {

    private Me me;
    private Links links;
    private String status;

    public static class Me {
        private String status;
        private String message;
        private String instanceId;
        private String explanation;
        private String instanceStatus;

        @JsonProperty("status")
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @JsonProperty("message")
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @JsonProperty("instanceId")
        public String getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }

        @JsonProperty("explanation")
        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        @JsonProperty("instanceStatus")
        public String getInstanceStatus() {
            return instanceStatus;
        }

        public void setInstanceStatus(String instanceStatus) {
            this.instanceStatus = instanceStatus;
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    @NoArgsConstructor
    public static class Links {
        private String self;

        @JsonProperty("self")
        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }
}
