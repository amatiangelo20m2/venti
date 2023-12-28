package com.venticonsulting.waapi.entity.waapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCodeResponse {

    @JsonProperty("qrCode")
    private QrCode qrCode;

    @JsonProperty("links")
    private Links links;

    @JsonProperty("status")
    private String status;

    public void setQrCode(QrCode qrCode) {
        this.qrCode = qrCode;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Inner class for qrCode
    public static class QrCode {

        @JsonProperty("status")
        private String status;

        @JsonProperty("instanceId")
        private String instanceId;

        @JsonProperty("data")
        private Data data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    // Inner class for data
    public static class Data {

        @JsonProperty("qr_code")
        private String qrCode;

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }
    }

    // Inner class for links
    public static class Links {

        @JsonProperty("self")
        private String self;

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }
}
