package com.venticonsulting.authservice.entity.dto;

import com.venticonsulting.authservice.entity.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class UpdateUserEntity {
    private long userId;
    private String name;
    private String lastname;
    private String phone;
    private String email;
    private String avatar;
    private ProfileStatus profileStatus;
}
