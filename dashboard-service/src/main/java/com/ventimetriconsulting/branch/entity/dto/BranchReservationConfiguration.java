package com.ventimetriconsulting.branch.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class BranchReservationConfiguration {

    private String branchCode;
    private String cazzone;

}
