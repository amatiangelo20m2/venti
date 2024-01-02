package com.venticonsulting.waapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "branch_time_ranges")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchTimeRange {
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
    @JoinColumn(name = "restaurant_conf_id", nullable = false)
    private RestaurantConfiguration restaurantConfiguration;

    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "particular_date")
    private LocalDate particularDate;

    @ElementCollection
    @CollectionTable(
            name = "time_ranges",
            joinColumns = @JoinColumn(name = "branch_time_range_id")
    )
    @OrderColumn(name = "position")
    private List<TimeRange> timeRanges;

    @Column(name = "closed", nullable = false)
    private boolean isOpen = false;
}
