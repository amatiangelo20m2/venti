package com.venticonsulting.customerservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CustomerUpdateEntity {
    private long userId;
    private String name;
    private String phone;
    private String email;
    private boolean dataTreatment;
}
