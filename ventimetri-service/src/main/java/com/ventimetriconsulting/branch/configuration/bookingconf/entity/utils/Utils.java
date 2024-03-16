package com.ventimetriconsulting.branch.configuration.bookingconf.entity.utils;

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Utils {
    public static boolean isThisDateGraterThanNOWOfGivingMinuteValue(Date date, int minutes) {

        if (date == null) {
            return true;
        }

        Instant instant = date.toInstant();
        Instant now = Instant.now();

        long minutesDifference = ChronoUnit.MINUTES.between(instant, now);
        return minutesDifference > minutes;
    }

    public static boolean isLocalTimeInRange(LocalTime timeToCheck, LocalTime startTime, LocalTime endTime) {
        return !timeToCheck.isBefore(startTime) && !timeToCheck.isAfter(endTime);
    }
}
