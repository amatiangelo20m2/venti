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
    private int bookingSlotInMinutes;
    private boolean isReservationConfirmedManually;
    private int guestReceivingAuthConfirm;
    private int minBeforeSendConfirmMessage;
    private WaApiConfigDTO waApiConfigDTO;
    private List<BranchTimeRangeDTO> branchTimeRanges;

    public static RestaurantConfigurationDTO fromEntity(RestaurantConfiguration restaurantConfiguration) {
        return RestaurantConfigurationDTO.builder()
                .restaurantConfId(restaurantConfiguration.getRestaurantConfId())
                .branchCode(restaurantConfiguration.getBranchCode())
                .guests(restaurantConfiguration.getGuests())
                .bookingSlotInMinutes(restaurantConfiguration.getBookingSlotInMinutes())
                .isReservationConfirmedManually(restaurantConfiguration.isReservationConfirmedManually())
                .guestReceivingAuthConfirm(restaurantConfiguration.getGuestReceivingAuthConfirm())
                .minBeforeSendConfirmMessage(restaurantConfiguration.getMinBeforeSendConfirmMessage())
                .waApiConfigDTO(WaApiConfigDTO.fromEntity(restaurantConfiguration.getWaApiConfig()))
                .branchTimeRanges(BranchTimeRangeDTO.convertList(restaurantConfiguration.getBranchTimeRanges()))
                .build();
    }

}
