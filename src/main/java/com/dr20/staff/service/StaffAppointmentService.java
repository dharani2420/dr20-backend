package com.dr20.staff.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ForbiddenException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.User;
import com.dr20.common.enums.AppointmentStatus;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public Map<String, Object> dashboard(String userId) {
        User staff = getStaff(userId);
        String doctorId = staff.getLinkedProfileId();
        String today = LocalDate.now().toString();

        long waiting = appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId, today, List.of(AppointmentStatus.CONFIRMED, AppointmentStatus.UPCOMING, AppointmentStatus.VERIFIED));

        long completed = appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId, today, List.of(AppointmentStatus.COMPLETED));

        List<Appointment> upcoming = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, today)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED && a.getStatus() != AppointmentStatus.COMPLETED)
                .sorted(Comparator.comparing(Appointment::getAppointmentTime))
                .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("staffName", staff.getFirstName() + " " + (staff.getLastName() != null ? staff.getLastName() : ""));
        res.put("profession", staff.getProfession());
        res.put("todayOverview", Map.of("waiting", waiting, "completed", completed));
        res.put("upcomingAppointments", upcoming);
        return res;
    }

    public List<Appointment> upcoming(String userId) {
        return filterByStaff(userId, "upcoming");
    }

    public List<Appointment> past(String userId) {
        return filterByStaff(userId, "past");
    }

    public List<Appointment> cancelled(String userId) {
        String doctorId = getStaff(userId).getLinkedProfileId();
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    public List<Appointment> search(String userId, String q) {
        String doctorId = getStaff(userId).getLinkedProfileId();
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream()
                .filter(a -> a.getPatientName() != null && a.getPatientName().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Appointment getById(String userId, String appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        verifyOwnership(userId, appt);
        return appt;
    }

    public Appointment verifyQr(String userId, String qrData) {
        Appointment appt = appointmentRepository.findByQrData(qrData)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid QR code"));
        verifyOwnership(userId, appt);
        return markVerified(appt);
    }

    public Appointment verifyToken(String userId, String tokenNumber) {
        Appointment appt = appointmentRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
        verifyOwnership(userId, appt);
        return markVerified(appt);
    }

    public Appointment start(String userId, String appointmentId) {
        Appointment appt = getById(userId, appointmentId);
        if (appt.getStatus() != AppointmentStatus.VERIFIED && appt.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Verify patient first");
        }
        appt.setStatus(AppointmentStatus.IN_PROGRESS);
        appt.setStartedAt(LocalDateTime.now());
        return appointmentRepository.save(appt);
    }

    public Appointment complete(String userId, String appointmentId) {
        Appointment appt = getById(userId, appointmentId);
        if (appt.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new BadRequestException("Appointment must be in progress");
        }
        appt.setStatus(AppointmentStatus.COMPLETED);
        appt.setCompletedAt(LocalDateTime.now());
        return appointmentRepository.save(appt);
    }

    private Appointment markVerified(Appointment appt) {
        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment cancelled");
        }
        appt.setStatus(AppointmentStatus.VERIFIED);
        appt.setVerifiedAt(LocalDateTime.now());
        return appointmentRepository.save(appt);
    }

    private List<Appointment> filterByStaff(String userId, String type) {
        String doctorId = getStaff(userId).getLinkedProfileId();
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream()
                .filter(a -> {
                    LocalDate d = LocalDate.parse(a.getAppointmentDate());
                    if ("past".equals(type)) {
                        return d.isBefore(today) || a.getStatus() == AppointmentStatus.COMPLETED;
                    }
                    return !d.isBefore(today) && a.getStatus() != AppointmentStatus.CANCELLED
                            && a.getStatus() != AppointmentStatus.COMPLETED;
                })
                .collect(Collectors.toList());
    }

    private User getStaff(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getRole().isStaff() || user.getLinkedProfileId() == null) {
            throw new ForbiddenException("Staff profile not linked");
        }
        return user;
    }

    private void verifyOwnership(String userId, Appointment appt) {
        User staff = getStaff(userId);
        if (!appt.getDoctorId().equals(staff.getLinkedProfileId())) {
            throw new ForbiddenException("Not your appointment");
        }
    }
}
