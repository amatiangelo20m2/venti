package com.venticonsulting.waapi.entity.dto;

import com.venticonsulting.waapi.entity.configuration.TimeRange;
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
        return TimeRange.builder()
                .startTime(LocalTime.of(timeRangeUpdateRequest.startTimeHour, timeRangeUpdateRequest.startTimeMinutes, 0))
                .endTime(LocalTime.of(timeRangeUpdateRequest.endTimeHour, timeRangeUpdateRequest.endTimeMinutes, 0))
                .build();
    }

    public static List<TimeRange> convertTimeRange(List<TimeRangeUpdateRequest> timeRangeUpdateRequestList){
        List<TimeRange> timeRanges = new ArrayList<>();

        for(TimeRangeUpdateRequest timeRangeUpdateRequest : timeRangeUpdateRequestList){
            timeRanges.add(convertTimeRange(timeRangeUpdateRequest));
        }

        return timeRanges;
    }
}
