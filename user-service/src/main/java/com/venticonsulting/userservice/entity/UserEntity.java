package com.venticonsulting.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "UserEntity")
@Table(name = "USER_ENTITY",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"user_id", "email"}))
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class UserEntity {

        @Id
        @SequenceGenerator(
                name = "user_id",
                sequenceName = "user_id",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "user_id"
        )
        @Column(
                name = "user_id",
                updatable = false
        )
        private long userId;

        private String name;
        private String lastname;

        @Column(
                name = "phone",
                unique = true
        )
        private String phone;
        @Column(
                name = "email",
                unique = true,
                nullable = false
        )
        private String email;

}
