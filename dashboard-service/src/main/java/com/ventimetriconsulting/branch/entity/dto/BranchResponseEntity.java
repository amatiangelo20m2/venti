package com.ventimetriconsulting.branch.entity.dto;

import com.ventimetriconsulting.branch.entity.BranchConfiguration;
import com.ventimetriconsulting.branch.entity.BranchSchedule;
import com.ventimetriconsulting.branch.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class BranchResponseEntity {

    private String name;
    private String address;
    private String email;
    private String phone;
    private String vat;
    private BranchType type;
    private String branchCode;
    private Role role;

    private BranchConfiguration branchConfiguration;
    private BranchSchedule branchSchedule;

}
