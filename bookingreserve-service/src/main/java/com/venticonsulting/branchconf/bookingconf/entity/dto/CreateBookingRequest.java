package com.venticonsulting.branchconf.bookingconf.entity.dto;

import com.venticonsulting.branchconf.bookingconf.entity.booking.Customer;
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
    int guests;
    int child;
    int dogsAllowed;
    String particularRequests;
    String date;
    String time;
    long customerId;
    String branchName;
    String branchAddress;
}
