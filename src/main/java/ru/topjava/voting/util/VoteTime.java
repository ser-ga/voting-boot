package ru.topjava.voting.util;

import java.time.LocalTime;

public class VoteTime {

    private static int hour;
    private static int minute;

    private static LocalTime time;

    public VoteTime(int hh, int mm) {
        hour = hh;
        minute = mm;
        time = LocalTime.of(hour, minute);
    }

    public static LocalTime getTime() {
        return time;
    }

    public static void setTime(LocalTime time) {
        VoteTime.time = time;
    }

    public static void restore() {
        time = LocalTime.of(hour, minute);
    }

    @Override
    public String toString() {
        return getTime().toString();
    }
}
