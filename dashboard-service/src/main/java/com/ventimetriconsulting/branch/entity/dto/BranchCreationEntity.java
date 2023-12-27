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

    private String name;
    private String address;
    private String email;
    private String phone;
    private String vat;
    private BranchType type;
    private String userCode;
}