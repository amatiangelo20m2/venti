package com.venticonsulting.bookingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;

@Document(collection = "calendar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {

    @Id
    private String calendarId;
    private String branchCode;
    private Map<DayOfWeek, OpeningHours> openingHours;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OpeningHours {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}