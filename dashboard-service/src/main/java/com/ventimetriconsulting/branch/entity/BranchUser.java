package com.ventimetriconsulting.branch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "BranchUser")
@Table(name = "BRANCH_USER",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"branch_code", "user_code", "role"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchUser {

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_code")
    private Branch branch;

    @Column(name = "user_code")
    private String userCode;

    private Role role;
}
