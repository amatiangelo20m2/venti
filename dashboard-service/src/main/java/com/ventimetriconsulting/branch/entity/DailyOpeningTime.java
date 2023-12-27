package com.ventimetriconsulting.branch.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyOpeningTime {

    private LocalTime openingTime;

    private LocalTime closingTime;

}