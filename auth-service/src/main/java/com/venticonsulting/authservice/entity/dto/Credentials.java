package com.venticonsulting.authservice.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Credentials {
    private String email;
    private String password;
}
