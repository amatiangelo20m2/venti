package com.venticonsulting.bookingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;

@Document(value = "customer")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Customer {

        @Id
        private String customerId;
        private String name;
        private String phone;
        private String email;
        private Date dob;
        private boolean dataTreatment;
}
