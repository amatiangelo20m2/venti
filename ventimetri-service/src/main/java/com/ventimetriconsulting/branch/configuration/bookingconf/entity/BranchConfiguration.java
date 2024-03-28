package com.ventimetriconsulting.branch.configuration.bookingconf.entity;

import com.ventimetriconsulting.branch.entity.Branch;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "BranchConfiguration")
@Table(name = "branch_configuration",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"branch_conf_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Accessors(chain = true)
public class BranchConfiguration implements Serializable {

    @Id
    @SequenceGenerator(
            name = "branch_conf_id",
            sequenceName = "branch_conf_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "branch_conf_id"
    )
    @Column(
            name = "branch_conf_id",
            updatable = false
    )
    private Long branchConfId;

    @Column(name = "guests")
    private int guests = 0;

    @Column(name = "booking_slot_in_minutes")
    private int bookingSlotInMinutes = 0;

    @Column(name = "is_reservation_confirmed_manually")
    private boolean isReservationConfirmedManually = false;

    @Column(name = "guest_receiving_auth_confirm")
    private int guestReceivingAuthConfirm = 0;

    @Column(name = "min_before_send_confirm_message")
    private int minBeforeSendConfirmMessage = 0;

    @Column(name = "max_table_number")
    private int maxTableNumber = 0;

    @Column(name = "contact_id")
    private String contactId = "";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "form_tag_id")
    private List<FormTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "branchConfiguration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BookingForm> bookingForms = new ArrayList<>();

    private String instanceId;
    private String displayName;
    private String formattedNumber;
    private String profilePicUrl;

    @Column(name = "last_qr_code", columnDefinition = "TEXT")
    private String lastQrCode;
    private String owner;
    private Date instanceCreationDate;
    private Date instanceUpdateDate;
    private String instanceStatus;
    private String message;
    private String explanation;

    @Column(name = "last_wa_api_check")
    private Date lastWaApiConfCheck;

    private Date branchConfCreationDate;
    @Column(name = "dogs_allowed")
    private int dogsAllowed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = true)
    private Branch branch;
}
