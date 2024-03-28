package com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BranchGeneralConfigurationEditRequest {
    private String branchCode;
    private int guests;
    private int bookingSlotInMinutes;
    private boolean isReservationConfirmedManually;
    private int guestReceivingAuthConfirm;
    private int maxTableNumber;
    private int minBeforeSendConfirmMessage;
    private int dogsAllowed;
}
