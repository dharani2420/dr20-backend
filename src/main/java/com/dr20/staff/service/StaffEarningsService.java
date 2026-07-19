package com.dr20.staff.service;

import com.dr20.common.enums.AppointmentStatus;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffEarningsService {

    private final AppointmentRepository appointmentRepository;

    public Map<String, Object> getSummary(String doctorId) {
        List<Appointment> completed = appointmentRepository
                .findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .collect(Collectors.toList());

        double total = completed.stream()
                .mapToDouble(a -> a.getConsultationFee() != null ? a.getConsultationFee() : 0)
                .sum();

        String today = LocalDate.now().toString();
        String monthPrefix = today.substring(0, 7);

        double todayEarnings = completed.stream()
                .filter(a -> today.equals(a.getAppointmentDate()))
                .mapToDouble(a -> a.getConsultationFee() != null ? a.getConsultationFee() : 0)
                .sum();

        double monthEarnings = completed.stream()
                .filter(a -> a.getAppointmentDate() != null && a.getAppointmentDate().startsWith(monthPrefix))
                .mapToDouble(a -> a.getConsultationFee() != null ? a.getConsultationFee() : 0)
                .sum();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("totalEarnings", total);
        res.put("todayEarnings", todayEarnings);
        res.put("thisMonth", monthEarnings);
        res.put("monthEarnings", monthEarnings);
        res.put("completedAppointments", completed.size());
        res.put("automaticPayout", Map.of(
                "title", "Automatic Payout",
                "message", "Your earnings are automatically transferred to your registered bank account every Tuesday."
        ));
        return res;
    }

    public List<Map<String, Object>> getRecentTransactions(String doctorId) {
        List<Appointment> completed = appointmentRepository
                .findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .limit(10)
                .collect(Collectors.toList());

        List<Map<String, Object>> txs = new ArrayList<>();
        String today = LocalDate.now().toString();
        for (Appointment a : completed) {
            Map<String, Object> tx = new LinkedHashMap<>();
            tx.put("appointmentId", a.getId());
            tx.put("patientName", a.getPatientName());
            tx.put("consultationType", a.getConsultationType());
            tx.put("amount", a.getConsultationFee());
            tx.put("date", a.getAppointmentDate());
            tx.put("time", a.getAppointmentTime());
            tx.put("displayDate", formatDisplayDate(a.getAppointmentDate(), a.getAppointmentTime(), today));
            txs.add(tx);
        }
        return txs;
    }

    private String formatDisplayDate(String date, String time, String today) {
        if (date == null) return time;
        if (today.equals(date)) {
            return "Today - " + (time != null ? time : "");
        }
        return date + (time != null ? " - " + time : "");
    }
}
