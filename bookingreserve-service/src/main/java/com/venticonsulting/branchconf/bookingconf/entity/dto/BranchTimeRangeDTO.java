package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.configuration.BranchTimeRange;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.TimeRange;
import com.venticonsulting.branchconf.bookingconf.entity.utils.WeekDayItalian;
import lombok.*;

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
        if (branchTimeRanges == null || branchTimeRanges.isEmpty()) {
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
        BranchTimeRange branchTimeRange = new BranchTimeRange();

        branchTimeRange.setBranchTimeRangeId(branchTimeRangeDTO.getId());
        branchTimeRange.setClosed(branchTimeRangeDTO.isClosed());
        branchTimeRange.setDayOfWeek(branchTimeRangeDTO.getDayOfWeek());
        branchTimeRange.setTimeRanges(branchTimeRangeDTO.getTimeRanges());
        branchTimeRange.setParticularDate(branchTimeRangeDTO.getParticularDate());

        return branchTimeRange;
    }

}
