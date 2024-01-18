package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.configuration.BookingForm;
import com.venticonsulting.branchconf.bookingconf.entity.configuration.BranchConfiguration;
import com.venticonsulting.branchconf.waapiconf.dto.WaApiConfigDTO;
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
    private List<BookingFormDto> bookingFormList;


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
                .bookingFormList(BookingFormDto.convertList(branchConfiguration.getBookingForms()))
                .build();
    }

}
