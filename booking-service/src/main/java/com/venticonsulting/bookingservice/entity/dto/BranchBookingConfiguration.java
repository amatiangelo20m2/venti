package com.venticonsulting.bookingservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class BranchBookingConfiguration {
    private String branchCode;
    private String branchName;
    private String address;
    private String phone;
}
