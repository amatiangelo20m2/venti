package com.ventimetriconsulting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venticonsulting.branchconf.waapiconf.dto.CreateUpdateResponse;
import com.venticonsulting.branchconf.waapiconf.dto.MeResponse;
import com.venticonsulting.branchconf.waapiconf.dto.QrCodeResponse;

import java.util.UUID;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CreateUpdateResponse convertJsonToCreateUpdateResponse(String json) {
        try {
            return objectMapper.readValue(json, CreateUpdateResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to CreateUpdateResponse", e);
        }
    }

    public static MeResponse convertMeResponse(String json) {
        try {
            return objectMapper.readValue(json, MeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to CreateUpdateResponse", e);
        }
    }

    public static QrCodeResponse convertQrResponse(String json) {
        try {
            return objectMapper.readValue(json, QrCodeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to CreateUpdateResponse", e);
        }
    }

    public static String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "B" + uuid.substring(0, 9).toUpperCase();
    }

}
