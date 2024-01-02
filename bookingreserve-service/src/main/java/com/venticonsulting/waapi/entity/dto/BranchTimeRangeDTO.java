package com.venticonsulting.waapi.entity.dto;

import com.venticonsulting.waapi.entity.BranchTimeRange;
import com.venticonsulting.waapi.entity.TimeRange;
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
    private DayOfWeek dayOfWeek;
    private List<TimeRange> timeRanges;
    private LocalDate particularDate;
    private boolean isOpen;

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
                .id(branchTimeRange.getBranchTimeRangeId())
                .dayOfWeek(branchTimeRange.getDayOfWeek())
                .timeRanges(branchTimeRange.getTimeRanges())
                .particularDate(branchTimeRange.getParticularDate())
                .isOpen(branchTimeRange.isOpen())
                .build();
    }

}
