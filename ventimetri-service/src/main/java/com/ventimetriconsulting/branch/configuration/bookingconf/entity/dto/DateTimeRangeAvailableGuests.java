package com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.TimeRange;
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
