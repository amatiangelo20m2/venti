package com.venticonsulting.waapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "restaurant_configuration",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"restaurant_conf_id", "branch_code"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RestaurantConfiguration implements Serializable {

    @Id
    @SequenceGenerator(
            name = "restaurant_conf_id",
            sequenceName = "restaurant_conf_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restaurant_conf_id"
    )
    @Column(
            name = "restaurant_conf_id",
            updatable = false
    )
    private Long restaurantConfId;

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

    @OneToMany(mappedBy = "restaurantConfiguration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BranchTimeRange> branchTimeRanges;

    @OneToOne(mappedBy = "restaurantConfiguration", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private WaApiConfigEntity waApiConfig;

    private Date creationDate;

}
