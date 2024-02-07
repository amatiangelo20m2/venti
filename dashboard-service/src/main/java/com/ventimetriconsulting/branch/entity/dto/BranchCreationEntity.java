package com.ventimetriconsulting.branch.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class BranchCreationEntity {

    private String branchCode;
    private String userCode;
    private String name;
    private String email;
    private String address;
    private String city;
    private String cap;
    private String phoneNumber;
    private String vat;
    private BranchType type;
    private byte[] logoImage;
}