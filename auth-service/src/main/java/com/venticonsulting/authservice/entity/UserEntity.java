package com.venticonsulting.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "UserEntity")
@Table(name = "USER_ENTITY", uniqueConstraints=@UniqueConstraint(columnNames={"id", "email", "user_code"}))
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class UserEntity {
        @Id
        @SequenceGenerator(
                name = "id",
                sequenceName = "id",
                allocationSize = 1
        )
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "id"
        )
        @Column(
                name = "id",
                updatable = false
        )
        private long id;
        private String name;
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
        @Column(
                name = "user_code",
                unique = true,
                length = 10
        )
        private String userCode;
        private String avatar;
        private ProfileStatus profileStatus;
        @Column(length = 60)
        private String password;
        private SignInMethod signInMethod;

        @PrePersist
        public void generateUniqueCode() {
                this.userCode = generateUniqueHexCode();
        }

        private String generateUniqueHexCode() {
                String uuid = UUID.randomUUID().toString().replace("-", "");
                return "U" + uuid.substring(0, 9).toUpperCase();
        }
}
