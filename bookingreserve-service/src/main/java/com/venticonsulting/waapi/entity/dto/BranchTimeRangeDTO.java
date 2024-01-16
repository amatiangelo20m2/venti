package com.venticonsulting.waapi.entity.dto;

import com.venticonsulting.waapi.entity.BranchTimeRange;
import com.venticonsulting.waapi.entity.TimeRange;
import com.venticonsulting.waapi.entity.utils.WeekDayItalian;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchTimeRangeDTO {
    private Long id;
    private WeekDayItalian dayOfWeek;
    private boolean isClosed;
    private List<TimeRange> timeRanges;
    private LocalDate particularDate;

    public static List<BranchTimeRangeDTO> convertList(List<BranchTimeRange> branchTimeRanges) {
        if (branchTimeRanges == null) {
            return Collections.emptyList();
        }
        return branchTimeRanges.stream()
                .map(BranchTimeRangeDTO::convert)
                .collect(Collectors.toList());
    }

    public static BranchTimeRangeDTO convert(BranchTimeRange branchTimeRange) {
        return BranchTimeRangeDTO.builder()
                .isClosed(branchTimeRange.isClosed())
                .id(branchTimeRange.getBranchTimeRangeId())
                .dayOfWeek(branchTimeRange.getDayOfWeek())
                .timeRanges(branchTimeRange.getTimeRanges())
                .particularDate(branchTimeRange.getParticularDate())
                .build();
    }

    public static List<BranchTimeRange> convertListToEntity(List<BranchTimeRangeDTO> branchTimeRangeDTOs) {
        if (branchTimeRangeDTOs == null) {
            return Collections.emptyList();
        }
        return branchTimeRangeDTOs.stream()
                .map(BranchTimeRangeDTO::convertToEntity)
                .collect(Collectors.toList());
    }

    public static BranchTimeRange convertToEntity(BranchTimeRangeDTO branchTimeRangeDTO) {
        return BranchTimeRange.builder()
                .branchTimeRangeId(branchTimeRangeDTO.getId())
                .isClosed(branchTimeRangeDTO.isClosed())
                .dayOfWeek(branchTimeRangeDTO.getDayOfWeek())
                .timeRanges(branchTimeRangeDTO.getTimeRanges())
                .particularDate(branchTimeRangeDTO.getParticularDate())
                .build();
    }

}
