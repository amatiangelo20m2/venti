package com.venticonsulting.branchconf.bookingconf.entity.dto;

import lombok.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class CustomerFormData implements Serializable {

    private String branchCode;
    private String branchName;
    private String email;
    private String phone;
    private String formCode;
    private String formLogo;
    private int guests;
    private int bookingSlotInMinutes;
    private int maxTableNumber;
    private String address;
    private int dogsAllowed;
    private List<BranchTimeRangeDTO> branchTimeRangeDTOS;
    private List<DateTimeRangeAvailableGuests> dateTimeRangeAvailableGuests;

}
