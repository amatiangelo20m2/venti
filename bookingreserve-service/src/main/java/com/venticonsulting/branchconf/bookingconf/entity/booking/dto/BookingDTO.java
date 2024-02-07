package com.venticonsulting.branchconf.bookingconf.entity.booking.dto;


import com.venticonsulting.branchconf.bookingconf.entity.booking.Booking;
import com.venticonsulting.branchconf.bookingconf.entity.booking.Customer;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class BookingDTO {

    private Long bookingId;
    private String branchCode;
    private String bookingCode;
    private LocalDate date;
    private LocalTime time;
    private int guest;
    private int child;
    private int allowedDogs;
    private String requests;
    private String formCodeFrom;
    private Date insertBookingTime;
    private Customer customer;
    private BookingStatus bookingStatus;

    public BookingDTO(Booking booking) {
        this.bookingId = booking.getBookingId();
        this.branchCode = booking.getBranchCode();
        this.date = booking.getDate();
        this.time = booking.getTime();
        this.guest = booking.getGuest();
        this.bookingCode = booking.getBookingcode();
        this.child = booking.getChild();
        this.allowedDogs = booking.getAllowedDogs();
        this.requests = booking.getRequests();
        this.formCodeFrom = booking.getFormCodeFrom();
        this.insertBookingTime = booking.getInsertBookingTime();
        this.customer = booking.getCustomer();
        this.bookingStatus = booking.getBookingStatus();
    }


    public static List<BookingDTO> convertToList(List<Booking> bookings) {
        return bookings.stream().map(BookingDTO::new).collect(Collectors.toList());
    }

}
