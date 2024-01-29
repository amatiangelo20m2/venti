package com.venticonsulting.branchconf.bookingconf.entity.dto;

import lombok.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@ToString
public class BranchResponseEntity {

    private String name;
    private String address;
    private String email;
    private String phone;
    private String branchCode;
    private byte[] logoImage;
}
