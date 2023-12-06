package com.venticonsulting.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "UserEntity")
@Table(name = "USER_ENTITY", uniqueConstraints=@UniqueConstraint(columnNames={"id", "email"}))
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
        private String userid;
        private String avatar;
        private ProfileStatus profileStatus;
        @Column(length = 60)
        private String password;

        public static String encryptPassword(String password) {
                return BCrypt.hashpw(password, BCrypt.gensalt());
        }
        public boolean checkPassword(String enteredPassword) {
                return BCrypt.checkpw(enteredPassword, this.password);
        }
}
