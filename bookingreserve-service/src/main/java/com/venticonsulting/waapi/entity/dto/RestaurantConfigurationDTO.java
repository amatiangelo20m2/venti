package com.venticonsulting.waapi.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantConfigurationDTO {
    private String branchCode;
    private int guests;
    private boolean allowOverbooking;
    private WaApiConfigDTO waApiConfigDTO;
    private List<BranchTimeRangeDTO> branchTimeRanges;
    private int bookingSlotInMinutes;
}
