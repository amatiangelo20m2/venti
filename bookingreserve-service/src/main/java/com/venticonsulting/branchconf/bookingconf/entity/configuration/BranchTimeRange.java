package com.venticonsulting.branchconf.bookingconf.entity.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.venticonsulting.branchconf.bookingconf.entity.booking.Booking;
import com.venticonsulting.branchconf.bookingconf.entity.utils.WeekDayItalian;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "branch_time_ranges")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchTimeRange implements Serializable {
    @Id
    @SequenceGenerator(
            name = "branch_time_range_id",
            sequenceName = "branch_time_range_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "branch_time_range_id"
    )
    @Column(
            name = "branch_time_range_id",
            updatable = false
    )
    private Long branchTimeRangeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_form_id", nullable = false)
    private BookingForm bookingForm;

    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private WeekDayItalian dayOfWeek;

    @Column(name = "is_closed")
    private boolean isClosed = true;

    @Column(name = "particular_date")
    private LocalDate particularDate;

    @ElementCollection
    @CollectionTable(
            name = "time_ranges",
            joinColumns = @JoinColumn(name = "branch_time_range_id")
    )
    @OrderColumn(name = "position")
    private List<TimeRange> timeRanges;
}
