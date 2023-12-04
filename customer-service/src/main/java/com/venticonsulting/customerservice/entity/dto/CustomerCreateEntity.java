package com.venticonsulting.customerservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class CustomerCreateEntity {
    private String name;
    private String lastname;
    private String phone;
    private String email;
    private boolean dataTreatment;
}
