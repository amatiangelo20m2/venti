package com.venticonsulting.customerservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CustomerResponseEntity {
    private String name;
    private String lastname;
    private String phone;
    private String email;
    private boolean dataTreatment;
}
