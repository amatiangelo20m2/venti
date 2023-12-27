package com.venticonsulting.authservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@Builder
@ToString
public class UserCreateEntity {
    private String name;
    private String phone;
    private String email;
    private String avatar;
}
