package com.venticonsulting.waapi.entity.dto;

import com.venticonsulting.waapi.entity.configuration.BranchConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchConfigurationDTO {
    private Long branchConfId;
    private String branchCode;
    private int guests;
    private int bookingSlotInMinutes;
    private boolean isReservationConfirmedManually;
    private int guestReceivingAuthConfirm;
    private int minBeforeSendConfirmMessage;
    private WaApiConfigDTO waApiConfigDTO;
    private List<BranchTimeRangeDTO> branchTimeRanges;

    public static BranchConfigurationDTO fromEntity(BranchConfiguration branchConfiguration) {
        return BranchConfigurationDTO.builder()
                .branchConfId(branchConfiguration.getBranchConfId())
                .branchCode(branchConfiguration.getBranchCode())
                .guests(branchConfiguration.getGuests())
                .bookingSlotInMinutes(branchConfiguration.getBookingSlotInMinutes())
                .isReservationConfirmedManually(branchConfiguration.isReservationConfirmedManually())
                .guestReceivingAuthConfirm(branchConfiguration.getGuestReceivingAuthConfirm())
                .minBeforeSendConfirmMessage(branchConfiguration.getMinBeforeSendConfirmMessage())
                .waApiConfigDTO(WaApiConfigDTO.fromEntity(branchConfiguration.getWaApiConfig()))
                .branchTimeRanges(BranchTimeRangeDTO.convertList(branchConfiguration.getBranchTimeRanges()))
                .build();
    }

}
