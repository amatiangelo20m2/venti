package com.ventimetriconsulting.branch.entity;

import com.ventimetriconsulting.branch.entity.dto.BranchType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.UUID;

@Entity(name = "Branch")
@Table(name = "BRANCH",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"branch_id", "name", "branch_code"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class Branch {

    @Id
    @SequenceGenerator(
            name = "branch_id",
            sequenceName = "branch_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "branch_id"
    )
    @Column(
            name = "branch_id",
            updatable = false
    )
    private long branchId;

    @Column(
            name = "branch_code",
            unique = true,
            length = 10
    )
    private String branchCode;
    private String name = "";
    private String email = "";
    private String address = "";
    @Column(
            name = "phone",
            unique = true)
    private String phoneNumber;
    private String vat = "";

    @Enumerated
    private BranchType type;

    @Lob
    private byte[] logoImage;

    @PrePersist
    public void generateUniqueCode() {
        this.branchCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "B" + uuid.substring(0, 9).toUpperCase();
    }
}
