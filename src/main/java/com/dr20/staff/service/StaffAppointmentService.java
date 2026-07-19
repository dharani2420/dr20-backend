package com.dr20.staff.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ForbiddenException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.MedicalRecord;
import com.dr20.shared.model.User;
import com.dr20.common.enums.AppointmentStatus;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.MedicalRecordRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.NotificationHelper;
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
    private final DoctorRepository doctorRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final NotificationHelper notificationHelper;

    public Map<String, Object> dashboard(String userId) {
        User staff = getStaff(userId);
        String doctorId = staff.getLinkedProfileId();
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        String today = LocalDate.now().toString();

        long todayAppointments = appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId, today, List.of(
                        AppointmentStatus.UPCOMING, AppointmentStatus.CONFIRMED,
                        AppointmentStatus.VERIFIED, AppointmentStatus.IN_PROGRESS,
                        AppointmentStatus.COMPLETED));

        long completedToday = appointmentRepository.countByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId, today, List.of(AppointmentStatus.COMPLETED));

        List<Appointment> upcoming = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, today)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED
                        && a.getStatus() != AppointmentStatus.COMPLETED)
                .sorted(Comparator.comparing(Appointment::getAppointmentTime,
                        Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());

        enrichCards(upcoming);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("staffName", StaffResponseHelper.staffDisplayName(doctor, staff.getFirstName(), staff.getLastName()));
        res.put("specialization", doctor != null ? doctor.getSpecialization() : staff.getProfession());
        res.put("profession", staff.getProfession());
        res.put("profileImage", staff.getProfileImage() != null ? staff.getProfileImage()
                : (doctor != null ? doctor.getProfileImage() : null));
        res.put("todayOverview", Map.of(
                "todayAppointments", todayAppointments,
                "completedToday", completedToday,
                "waiting", todayAppointments - completedToday,
                "completed", completedToday
        ));
        res.put("upcomingAppointments", upcoming);
        return res;
    }

    public Map<String, Object> bookingCounts(String userId) {
        String doctorId = getStaff(userId).getLinkedProfileId();
        Map<String, Object> counts = new LinkedHashMap<>();
        counts.put("upcomingCount", countUpcoming(doctorId));
        counts.put("completedCount", appointmentRepository.countByDoctorIdAndStatus(
                doctorId, AppointmentStatus.COMPLETED));
        return counts;
    }

    public List<Appointment> upcoming(String userId) {
        List<Appointment> items = filterByStaff(userId, "upcoming");
        enrichCards(items);
        return items;
    }

    public List<Appointment> past(String userId) {
        String doctorId = getStaff(userId).getLinkedProfileId();
        List<Appointment> items = appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .peek(a -> a.setStatusLabel(StaffResponseHelper.toStatusLabel(a.getStatus())))
                .collect(Collectors.toList());
        return items;
    }

    public List<Appointment> cancelled(String userId) {
        String doctorId = getStaff(userId).getLinkedProfileId();
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED)
                .peek(a -> a.setStatusLabel(StaffResponseHelper.toStatusLabel(a.getStatus())))
                .collect(Collectors.toList());
    }

    public List<Appointment> search(String userId, String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        String doctorId = getStaff(userId).getLinkedProfileId();
        String query = q.trim().toLowerCase();
        List<Appointment> results = appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream()
                .filter(a -> matchesSearch(a, query))
                .collect(Collectors.toList());
        enrichCards(results);
        return results;
    }

    public Appointment getById(String userId, String appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        verifyOwnership(userId, appt);
        appt.setStatusLabel(StaffResponseHelper.toStatusLabel(appt.getStatus()));
        return appt;
    }

    public Map<String, Object> getDetail(String userId, String appointmentId) {
        Appointment appt = getById(userId, appointmentId);
        User patient = userRepository.findById(appt.getUserId()).orElse(null);
        Doctor doctor = doctorRepository.findById(appt.getDoctorId()).orElse(null);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("appointment", appt);
        detail.put("statusLabel", appt.getStatusLabel());
        detail.put("patientInfo", Map.of(
                "gender", firstNonNull(appt.getPatientGender(), patient != null ? patient.getGender() : null),
                "age", appt.getPatientAge(),
                "bloodGroup", firstNonNull(appt.getPatientBloodGroup(),
                        patient != null ? patient.getBloodGroup() : null)
        ));
        detail.put("patientMobile", StaffResponseHelper.formatPatientPhone(
                patient != null ? patient.getPhone() : null));
        detail.put("reasonForVisit", appt.getSymptoms());

        if (appt.getServiceAddress() != null) {
            double distanceKm = StaffResponseHelper.distanceKm(
                    doctor != null ? doctor.getLatitude() : null,
                    doctor != null ? doctor.getLongitude() : null,
                    appt.getServiceLatitude(),
                    appt.getServiceLongitude());
            detail.put("serviceAddress", Map.of(
                    "address", appt.getServiceAddress(),
                    "latitude", appt.getServiceLatitude(),
                    "longitude", appt.getServiceLongitude(),
                    "distanceKm", distanceKm,
                    "travelMinutes", StaffResponseHelper.estimateTravelMinutes(distanceKm)
            ));
        }
        return detail;
    }

    public Map<String, Object> getNavigate(String userId, String appointmentId) {
        return buildNavigationPayload(getDetail(userId, appointmentId));
    }

    public Map<String, Object> arrive(String userId, String appointmentId) {
        Appointment appt = getById(userId, appointmentId);
        appt.setArrivedAt(LocalDateTime.now());
        appointmentRepository.save(appt);

        Map<String, Object> detail = getDetail(userId, appointmentId);
        Map<String, Object> res = buildNavigationPayload(detail);
        res.put("arrivalConfirmed", true);
        res.put("arrivedAt", appt.getArrivedAt());
        res.put("appointmentId", appt.getId());
        return res;
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
        appt.setStatusLabel(StaffResponseHelper.toStatusLabel(appt.getStatus()));
        return appointmentRepository.save(appt);
    }

    public Appointment complete(String userId, String appointmentId, Map<String, String> body) {
        Appointment appt = getById(userId, appointmentId);
        if (appt.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new BadRequestException("Appointment must be in progress");
        }
        if (body != null) {
            if (body.get("consultationNotes") != null) appt.setConsultationNotes(body.get("consultationNotes"));
            if (body.get("prescription") != null) appt.setPrescription(body.get("prescription"));
        }
        appt.setStatus(AppointmentStatus.COMPLETED);
        appt.setCompletedAt(LocalDateTime.now());
        appt.setStatusLabel(StaffResponseHelper.toStatusLabel(appt.getStatus()));
        Appointment saved = appointmentRepository.save(appt);

        MedicalRecord record = new MedicalRecord();
        record.setUserId(appt.getUserId());
        record.setAppointmentId(appt.getId());
        record.setDoctorName(appt.getDoctorName());
        record.setTitle("Consultation — " + appt.getAppointmentDate());
        record.setType("VISIT_SUMMARY");
        record.setNotes(appt.getConsultationNotes());
        record.setFileUrl(appt.getPrescription() != null ? appt.getPrescription() : "");
        medicalRecordRepository.save(record);

        notificationHelper.notify(appt.getUserId(), "Consultation Completed",
                "Your visit with " + appt.getDoctorName() + " is complete. View your medical record.",
                "APPOINTMENT", appt.getId());
        return saved;
    }

    private Appointment markVerified(Appointment appt) {
        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment cancelled");
        }
        appt.setStatus(AppointmentStatus.VERIFIED);
        appt.setVerifiedAt(LocalDateTime.now());
        appt.setStatusLabel(StaffResponseHelper.toStatusLabel(appt.getStatus()));
        return appointmentRepository.save(appt);
    }

    private long countUpcoming(String doctorId) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(doctorId)
                .stream()
                .filter(a -> {
                    LocalDate d = LocalDate.parse(a.getAppointmentDate());
                    return !d.isBefore(today) && a.getStatus() != AppointmentStatus.CANCELLED
                            && a.getStatus() != AppointmentStatus.COMPLETED;
                })
                .count();
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

    private void enrichCards(List<Appointment> appointments) {
        for (int i = 0; i < appointments.size(); i++) {
            StaffResponseHelper.enrichAppointmentCard(appointments.get(i), i == 0);
        }
    }

    private boolean matchesSearch(Appointment appt, String query) {
        if (appt.getPatientName() != null && appt.getPatientName().toLowerCase().contains(query)) {
            return true;
        }
        if (appt.getTokenNumber() != null) {
            String token = appt.getTokenNumber().toLowerCase();
            String normalizedQuery = query.replace("#", "");
            return token.contains(normalizedQuery) || ("#" + token).contains(query);
        }
        return false;
    }

    private String firstNonNull(String primary, String fallback) {
        return primary != null ? primary : fallback;
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> buildNavigationPayload(Map<String, Object> detail) {
        Appointment appt = (Appointment) detail.get("appointment");
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("patientName", appt.getPatientName());
        res.put("consultationType", appt.getConsultationType());
        res.put("tokenNumber", appt.getTokenNumber());
        res.put("statusLabel", detail.get("statusLabel"));
        res.put("patientMobile", detail.get("patientMobile"));
        res.put("reasonForVisit", detail.get("reasonForVisit"));
        res.put("serviceAddress", detail.get("serviceAddress"));
        if (detail.get("serviceAddress") instanceof Map<?, ?> addr) {
            String address = addr.get("address") != null ? addr.get("address").toString() : null;
            Double lat = addr.get("latitude") instanceof Number n ? n.doubleValue() : null;
            Double lng = addr.get("longitude") instanceof Number n ? n.doubleValue() : null;
            if (address != null) {
                res.put("googleMapsUrl", "https://www.google.com/maps/search/?api=1&query="
                        + java.net.URLEncoder.encode(address, java.nio.charset.StandardCharsets.UTF_8));
            }
            if (lat != null && lng != null) {
                res.put("googleMapsUrl", "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
            }
        }
        return res;
    }
}
