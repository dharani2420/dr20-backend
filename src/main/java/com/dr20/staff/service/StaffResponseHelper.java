package com.dr20.staff.service;

import com.dr20.common.enums.AppointmentStatus;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Doctor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

final class StaffResponseHelper {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private StaffResponseHelper() {
    }

    static void enrichAppointmentCard(Appointment appt, boolean isNext) {
        appt.setStatusLabel(toStatusLabel(appt.getStatus()));
        appt.setCardLabel(isNext ? "Next Appointment" : "Upcoming Appointment");
        appt.setRelativeTime(toRelativeTime(appt.getAppointmentDate(), appt.getAppointmentTime()));
    }

    static String toStatusLabel(AppointmentStatus status) {
        if (status == null) return "Upcoming";
        return switch (status) {
            case UPCOMING, CONFIRMED -> "Upcoming";
            case VERIFIED -> "Verified";
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case CANCELLED -> "Cancelled";
        };
    }

    static String toRelativeTime(String date, String time) {
        LocalDateTime when = parseDateTime(date, time);
        if (when == null) return null;

        LocalDateTime now = LocalDateTime.now();
        if (when.isBefore(now)) return "Started";

        long totalMinutes = Duration.between(now, when).toMinutes();
        if (totalMinutes < 60) {
            return "In " + Math.max(1, totalMinutes) + " mins";
        }
        long hours = totalMinutes / 60;
        long mins = totalMinutes % 60;
        if (mins == 0) {
            return hours == 1 ? "In 1 hr" : "In " + hours + " hr";
        }
        return "In " + hours + " hr " + mins + " mins";
    }

    static LocalDateTime parseDateTime(String date, String time) {
        if (date == null || time == null) return null;
        try {
            LocalTime parsedTime = LocalTime.parse(time.trim(), TIME_FMT);
            return LocalDate.parse(date).atTime(parsedTime);
        } catch (Exception ignored) {
            return null;
        }
    }

    static double distanceKm(Double fromLat, Double fromLng, Double toLat, Double toLng) {
        if (fromLat == null || fromLng == null || toLat == null || toLng == null) return 0;
        double earthRadius = 6371;
        double dLat = Math.toRadians(toLat - fromLat);
        double dLng = Math.toRadians(toLng - fromLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(toLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return Math.round(earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 10.0) / 10.0;
    }

    static int estimateTravelMinutes(double distanceKm) {
        if (distanceKm <= 0) return 0;
        return (int) Math.max(1, Math.round(distanceKm * 3.75));
    }

    static String formatPatientPhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        if (phone.startsWith("+")) return phone;
        return "+91 " + phone;
    }

    static String staffDisplayName(Doctor doctor, String firstName, String lastName) {
        if (doctor != null && doctor.getName() != null && !doctor.getName().isBlank()) {
            return doctor.getName();
        }
        String name = ((firstName != null ? firstName : "") + " "
                + (lastName != null ? lastName : "")).trim();
        return name.isBlank() ? "Staff" : name;
    }
}
