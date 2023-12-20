package com.ventimetriconsulting.branch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity(name = "BranchSchedule")
@Table(name = "BRANCH_SCHEDULE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ElementCollection
    @CollectionTable(name = "daily_opening_times", joinColumns = @JoinColumn(name = "branch_schedule_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day_of_week")
    private Map<DayOfWeek, DailyOpeningTime> dailyOpeningTimes = new HashMap<>();


    private LocalTime openingTime;

    private LocalTime closingTime;

    private int maxPeopleAllowed;

}
