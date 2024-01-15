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
    private Long restaurantConfId;
    private String branchCode;
    private int guests;
    private boolean allowWaitingList;
    private boolean confirmReservation;
    private boolean allowOverlap;
    private boolean allowEditingBooking;
    private int bookingSlotInMinutes;
    private int minBeforeEditingReservationIsAllowed;
    private String recoveryNumber;
    private WaApiConfigDTO waApiConfigDTO;
    private List<BranchTimeRangeDTO> branchTimeRanges;

    public static RestaurantConfigurationDTO fromEntity(RestaurantConfiguration restaurantConfiguration) {
        return RestaurantConfigurationDTO.builder()
                .restaurantConfId(restaurantConfiguration.getRestaurantConfId())
                .branchCode(restaurantConfiguration.getBranchCode())
                .guests(restaurantConfiguration.getGuests())
                .allowWaitingList(restaurantConfiguration.isAllowWaitingList())
                .confirmReservation(restaurantConfiguration.isConfirmReservation())
                .allowOverlap(restaurantConfiguration.isAllowOverlap())
                .allowEditingBooking(restaurantConfiguration.isAllowEditingBooking())
                .bookingSlotInMinutes(restaurantConfiguration.getBookingSlotInMinutes())
                .minBeforeEditingReservationIsAllowed(restaurantConfiguration.getMinBeforeEditingReservationIsAllowed())
                .recoveryNumber(restaurantConfiguration.getRecoveryNumber())
                .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.getWaApiConfig()))
                .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.getBranchTimeRanges()))
                .build();
    }

}
