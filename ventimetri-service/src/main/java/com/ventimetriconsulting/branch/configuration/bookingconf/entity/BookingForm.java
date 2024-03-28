package com.ventimetriconsulting.branch.configuration.bookingconf.entity;

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
@Setter
@Getter
@Builder
//@Data
//@ToString
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

    private String address;

    private String formLogo;

    private String description;

    @Column(name = "form_type")
    private FormType formType;

    @Column(name = "redirect_page")
    private String redirectPage;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "tag_list")
    private String tagList;

    @ManyToOne
    @JoinColumn(name = "branch_conf_id", nullable = false)
    private BranchConfiguration branchConfiguration;

    @OneToMany(mappedBy = "bookingForm", cascade = CascadeType.ALL)
    private List<BranchTimeRange> branchTimeRanges;

    @PrePersist
    public void generateUniqueCode() {
        this.formCode = generateUniqueHexCode();
    }

    private String generateUniqueHexCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "F" + uuid;
    }

    public enum FormType {
        BOOKING_FORM, REDIRECT_FORM, EVENT
    }

}