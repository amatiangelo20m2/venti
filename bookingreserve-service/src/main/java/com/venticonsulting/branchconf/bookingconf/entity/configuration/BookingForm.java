package com.venticonsulting.branchconf.bookingconf.entity.configuration;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(name = "BookingForm")
@Table(name = "booking_form",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"booking_form_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BookingForm {

    @Id
    @SequenceGenerator(
            name = "booking_form_id",
            sequenceName = "booking_form_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "booking_form_id"
    )
    @Column(
            name = "booking_form_id",
            updatable = false
    )
    private Long bookingFormId;

    @Column(
            name = "form_code",
            unique = true
    )
    private String formCode;

    private String formName;

    private boolean isDefaultForm;

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
