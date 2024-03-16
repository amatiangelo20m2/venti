package com.ventimetriconsulting.branch.configuration.bookingconf.entity.utils;

import java.time.DayOfWeek;

public enum WeekDayItalian {
    LUNEDI,
    MARTEDI,
    MERCOLEDI,
    GIOVEDI,
    VENERDI,
    SABATO,
    DOMENICA,
    FESTIVO;

    public static WeekDayItalian fromEngDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return LUNEDI;
            case TUESDAY:
                return MARTEDI;
            case WEDNESDAY:
                return MERCOLEDI;
            case THURSDAY:
                return GIOVEDI;
            case FRIDAY:
                return VENERDI;
            case SATURDAY:
                return SABATO;
            case SUNDAY:
                return DOMENICA;
            default:
                throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
    }

}
