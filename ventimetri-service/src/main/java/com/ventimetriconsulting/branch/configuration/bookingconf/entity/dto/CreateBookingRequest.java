package com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto;

import lombok.*;

import java.io.Serializable;

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
