package com.venticonsulting.bookingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "booking")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Booking {

    @Id
    private String bookingId;
    @DBRef
    private Customer customer;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;
    private int number;
}
