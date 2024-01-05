package com.venticonsulting.waapi.entity.dto;

import com.venticonsulting.waapi.entity.RestaurantConfiguration;
import jakarta.persistence.Column;
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
    private boolean confirmReservation;
    private boolean allowOverlap;
    private boolean allowBookingDeletion;
    private int bookingSlotInMinutes;
    private int minBeforeDeleteReservationIsAllowed;
    private String recoveryNumber;
    private WaApiConfigDTO waApiConfigDTO;
    private List<BranchTimeRangeDTO> branchTimeRanges;

    public static RestaurantConfigurationDTO fromEntity(RestaurantConfiguration restaurantConfiguration) {
        return RestaurantConfigurationDTO.builder()
                .branchCode(restaurantConfiguration.getBranchCode())
                .guests(restaurantConfiguration.getGuests())
                .allowOverbooking(restaurantConfiguration.isAllowOverbooking())
                .confirmReservation(restaurantConfiguration.isConfirmReservation())
                .allowOverlap(restaurantConfiguration.isAllowOverlap())
                .allowBookingDeletion(restaurantConfiguration.isAllowBookingDeletion())
                .bookingSlotInMinutes(restaurantConfiguration.getBookingSlotInMinutes())
                .minBeforeDeleteReservationIsAllowed(restaurantConfiguration.getMinBeforeDeleteReservationIsAllowed())
                .recoveryNumber(restaurantConfiguration.getRecoveryNumber())
                .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.getWaApiConfig()))
                .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.getBranchTimeRanges()))
                .build();
    }

}
