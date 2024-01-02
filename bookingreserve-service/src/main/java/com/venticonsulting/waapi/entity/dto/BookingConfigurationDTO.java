package com.venticonsulting.waapi.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingConfigurationDTO {
    private String branchCode;

    private WaApiConfigDTO waApiConfigDTO;
    private RestaurantOpeningConfigurationDTO restaurantOpeningConfigurationDTO;
}
