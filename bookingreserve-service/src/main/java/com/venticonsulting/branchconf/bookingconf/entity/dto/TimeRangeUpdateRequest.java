package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeRangeUpdateRequest implements Serializable {

    private int startTimeHour;
    private int startTimeMinutes;
    private int endTimeHour;
    private int endTimeMinutes;

    public static TimeRange convertTimeRange(TimeRangeUpdateRequest timeRangeUpdateRequest){
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(LocalTime.of(timeRangeUpdateRequest.startTimeHour, timeRangeUpdateRequest.startTimeMinutes, 0));
        timeRange.setEndTime(LocalTime.of(timeRangeUpdateRequest.endTimeHour, timeRangeUpdateRequest.endTimeMinutes, 0));

        return timeRange;
    }

    public static List<TimeRange> convertTimeRange(List<TimeRangeUpdateRequest> timeRangeUpdateRequestList){
        List<TimeRange> timeRanges = new ArrayList<>();

        for(TimeRangeUpdateRequest timeRangeUpdateRequest : timeRangeUpdateRequestList){
            timeRanges.add(convertTimeRange(timeRangeUpdateRequest));
        }

        return timeRanges;
    }
}
