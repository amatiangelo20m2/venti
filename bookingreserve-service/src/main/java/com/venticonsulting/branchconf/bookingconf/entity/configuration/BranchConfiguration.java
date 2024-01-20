package com.venticonsulting.branchconf.bookingconf.entity.configuration;

import com.venticonsulting.branchconf.waapiconf.entity.WaApiConfigEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "BranchConfiguration")
@Table(name = "branch_configuration",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"branch_conf_id", "branch_code"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
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

    @Column(
            name = "branch_code",
            unique = true,
            length = 10
    )
    private String branchCode;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "branch_conf_id")
    private List<FormTag> tags;

    @OneToMany(mappedBy = "branchConfiguration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BookingForm> bookingForms;

    @OneToOne(mappedBy = "branchConfiguration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private WaApiConfigEntity waApiConfig;

    @Column(name = "last_wa_api_check")
    private Date lastWaApiConfCheck;

    private Date creationDate;

}
