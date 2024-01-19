package com.venticonsulting.branchconf.bookingconf.entity.configuration;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(name = "Form")
@Table(name = "form",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"form_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BookingForm {

    @Id
    @SequenceGenerator(
            name = "form_id",
            sequenceName = "form_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "form_id"
    )
    @Column(
            name = "form_id",
            updatable = false
    )
    private Long formId;

    @Column(
            name = "form_code",
            unique = true
    )
    private String formCode;

    private String formName;
    private boolean isDefaultForm;

    @Column(name = "form_type")
    private FormType formType;

    @Column(name = "redirect_page")
    private String redirectPage;

    @Column(name = "creation_date")
    private Date creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_conf_id", nullable = false)
    private BranchConfiguration branchConfiguration;

    @OneToMany(mappedBy = "bookingForm", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BranchTimeRange> branchTimeRanges;

    @PrePersist
    public void generateUniqueCode() {
        this.formCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "F" + uuid;
    }

}
