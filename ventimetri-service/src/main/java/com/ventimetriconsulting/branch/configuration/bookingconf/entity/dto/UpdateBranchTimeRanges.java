package com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBranchTimeRanges implements Serializable {
    private String branchCode;
    private List<Long> listConfIds;
    private List<TimeRangeUpdateRequest> timeRanges;

}
