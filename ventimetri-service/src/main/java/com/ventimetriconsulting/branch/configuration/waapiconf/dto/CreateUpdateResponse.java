package com.ventimetriconsulting.branch.configuration.waapiconf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@Builder
@Getter
@ToString
@NoArgsConstructor
public class CreateUpdateResponse {
    private Instance instance;
    private String status;

    public static class Instance {
        private String id;
        private String owner;
        private String webhookUrl;
        private String[] webhookEvents;
        private Boolean isTrial;

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("owner")
        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        @JsonProperty("webhook_url")
        public String getWebhookUrl() {
            return webhookUrl;
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }

        @JsonProperty("webhook_events")
        public String[] getWebhookEvents() {
            return webhookEvents;
        }

        public void setWebhookEvents(String[] webhookEvents) {
            this.webhookEvents = webhookEvents;
        }

        @JsonProperty("is_trial")
        public Boolean getIsTrial() {
            return isTrial;
        }

        public void setIsTrial(Boolean isTrial) {
            this.isTrial = isTrial;
        }
    }

}
