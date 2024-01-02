package com.venticonsulting.waapi.entity.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class RestaurantOpeningConfigurationDTO {
    private List<BranchTimeRangeDTO> branchTimeRanges;
}
