package com.venticonsulting.waapi.entity.dto;

import com.venticonsulting.waapi.entity.WaApiConfigEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaApiConfigDTO {
    private long waapiConfId;
    private String branchCode;
    private String instanceId;
    private String instanceStatus;
    private String displayName;
    private String formattedNumber;
    private String profilePicUrl;
    private String lastQrCode;
    private String owner;
    private String explanation;
    private String message;
    private Date creationDate;
    private Date updateDate;

    public static WaApiConfigDTO fromEntity(WaApiConfigEntity entity) {
        WaApiConfigDTO dto = new WaApiConfigDTO();
        dto.setWaapiConfId(entity.getWaapiConfId());
        dto.setBranchCode(entity.getBranchCode());
        dto.setInstanceId(entity.getInstanceId());
        dto.setInstanceStatus(entity.getInstanceStatus());
        dto.setDisplayName(entity.getDisplayName());
        dto.setFormattedNumber(entity.getFormattedNumber());
        dto.setProfilePicUrl(entity.getProfilePicUrl());
        dto.setLastQrCode(entity.getLastQrCode());
        dto.setOwner(entity.getOwner());
        dto.setCreationDate(entity.getCreationDate());
        dto.setUpdateDate(entity.getUpdateDate());
        dto.setExplanation(entity.getExplanation());
        dto.setMessage(entity.getMessage());
        return dto;
    }

}
