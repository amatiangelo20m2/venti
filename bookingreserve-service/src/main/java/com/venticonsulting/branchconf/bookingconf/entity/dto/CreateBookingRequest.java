package com.venticonsulting.branchconf.bookingconf.entity.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest implements Serializable {
    String branchCode;
    String formCode;
    String userEmail;
    String userPhone;
    String userName;
    String userLastName;
    LocalDate userDOB;
    int guests;
    String particularRequests;
    LocalDate date;
    LocalTime time;
    private boolean treatmentPersonalData;
}
