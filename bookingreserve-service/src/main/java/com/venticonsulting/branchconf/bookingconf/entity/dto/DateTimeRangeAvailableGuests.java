package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.TimeRange;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class DateTimeRangeAvailableGuests {

    private LocalDate date;
    private TimeRange timeRange;
    private int guestsAvailable;

}
