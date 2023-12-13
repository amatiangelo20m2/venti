package com.ventimetriconsulting.branch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "BranchConfiguration")
@Table(name = "BRANCH_CONFIGURATION")
@AllArgsConstructor
@Data
@Builder
@ToString
@NoArgsConstructor
public class BranchConfiguration {
    @Id
    @SequenceGenerator(
            name = "branch_config_id",
            sequenceName = "branch_config_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "branch_config_id"
    )
    @Column(
            name = "branch_config_id",
            updatable = false
    )
    private long branchConfId;

    private String waapiInstanceId;
    private String instanceStatus;

}
