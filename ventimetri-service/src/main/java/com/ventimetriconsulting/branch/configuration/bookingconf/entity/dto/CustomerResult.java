package com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto;


import com.ventimetriconsulting.branch.configuration.bookingconf.entity.booking.Customer;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class CustomerResult {

    private boolean isCustomerFound;
    private Customer customer;
    private String opt;
    private String profilePhoto;

}
