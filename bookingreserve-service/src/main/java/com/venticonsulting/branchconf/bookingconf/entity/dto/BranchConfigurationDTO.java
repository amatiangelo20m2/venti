package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.BranchConfiguration;
import com.venticonsulting.branchconf.bookingconf.entity.FormTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    private int maxTableNumber;
    private List<FormTag> tags;
    private String instanceId;
    private String displayName;
    private String contactId;
    private String formattedNumber;
    private String profilePicUrl;
    private String lastQrCode;
    private String owner;
    private Date instanceCreationDate;
    private Date instanceUpdateDate;
    private String instanceStatus;
    private String message;
    private String explanation;
    private Date lastWaApiConfCheck;
    private Date branchConfCreationDate;
    private List<BookingFormDto> bookingFormList;
    private int dogsAllowed;


    public static BranchConfigurationDTO fromEntity(BranchConfiguration branchConfiguration) {
        return BranchConfigurationDTO.builder()
                .branchConfId(branchConfiguration.getBranchConfId())
                .branchCode(branchConfiguration.getBranchCode())
                .guests(branchConfiguration.getGuests())
                .bookingSlotInMinutes(branchConfiguration.getBookingSlotInMinutes())
                .isReservationConfirmedManually(branchConfiguration.isReservationConfirmedManually())
                .guestReceivingAuthConfirm(branchConfiguration.getGuestReceivingAuthConfirm())
                .minBeforeSendConfirmMessage(branchConfiguration.getMinBeforeSendConfirmMessage())
                .tags(branchConfiguration.getTags())
                .instanceId(branchConfiguration.getInstanceId())
                .displayName(branchConfiguration.getDisplayName())
                .contactId(branchConfiguration.getContactId())
                .formattedNumber(branchConfiguration.getFormattedNumber())
                .owner(branchConfiguration.getOwner())
                .profilePicUrl(branchConfiguration.getProfilePicUrl())
                .lastQrCode(branchConfiguration.getLastQrCode())
                .owner(branchConfiguration.getOwner())
                .instanceCreationDate(branchConfiguration.getInstanceCreationDate())
                .instanceUpdateDate(branchConfiguration.getInstanceUpdateDate())
                .instanceStatus(branchConfiguration.getInstanceStatus())
                .message(branchConfiguration.getMessage())
                .explanation(branchConfiguration.getExplanation())
                .lastWaApiConfCheck(branchConfiguration.getLastWaApiConfCheck())
                .branchConfCreationDate(branchConfiguration.getBranchConfCreationDate())
                .bookingFormList(BookingFormDto.convertList(branchConfiguration.getBookingForms()))
                .maxTableNumber(branchConfiguration.getMaxTableNumber())
                .dogsAllowed(branchConfiguration.getDogsAllowed())
                .build();
    }
}
