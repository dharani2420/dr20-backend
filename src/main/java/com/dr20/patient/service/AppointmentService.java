package com.dr20.patient.service;

import com.dr20.common.config.AppConstants;
import com.dr20.common.exception.BadRequestException;
import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Doctor;
import com.dr20.common.enums.AppointmentStatus;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final AvailabilityService availabilityService;
    private final Random random = new Random();

    public Appointment book(Appointment req) {
        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (!availabilityService.bookSlot(req.getDoctorId(), req.getAppointmentDate(), req.getAppointmentTime())) {
            throw new BadRequestException("Slot not available");
        }

        try {
            double fee = doctor.getConsultationFee() != null ? doctor.getConsultationFee() : 500.0;
            req.setDoctorName(doctor.getName());
            req.setSpecialization(doctor.getSpecialization());
            req.setConsultationFee(fee);
            req.setPlatformFee(AppConstants.PLATFORM_FEE);
            req.setTotalFee(fee + AppConstants.PLATFORM_FEE);
            req.setStatus(AppointmentStatus.UPCOMING);
            req.setPaymentStatus("PENDING");
            req.setQrData("DR20-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            req.setTokenNumber(String.format("%04d", random.nextInt(10000)));
            return appointmentRepository.save(req);
        } catch (Exception e) {
            availabilityService.releaseSlot(req.getDoctorId(), req.getAppointmentDate(), req.getAppointmentTime());
            throw e;
        }
    }

    public Map<String, Object> getPass(String id) {
        Appointment appt = getById(id);
        Doctor doctor = doctorRepository.findById(appt.getDoctorId()).orElse(null);

        Map<String, Object> pass = new HashMap<>();
        pass.put("appointmentId", appt.getId());
        pass.put("qrData", appt.getQrData());
        pass.put("tokenNumber", appt.getTokenNumber());
        pass.put("doctorName", appt.getDoctorName());
        pass.put("date", appt.getAppointmentDate());
        pass.put("time", appt.getAppointmentTime());
        pass.put("patientName", appt.getPatientName());
        pass.put("status", appt.getStatus());

        if (doctor != null) {
            Map<String, Object> clinic = new HashMap<>();
            clinic.put("name", doctor.getClinicName());
            clinic.put("address", doctor.getClinicAddress());
            clinic.put("latitude", doctor.getLatitude());
            clinic.put("longitude", doctor.getLongitude());
            pass.put("clinic", clinic);
        }
        return pass;
    }

    public Appointment getById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    public List<Appointment> getUserAppointments(String userId) {
        return appointmentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Appointment> getUpcoming(String userId) {
        return filterUser(userId, true);
    }

    public List<Appointment> getPast(String userId) {
        return filterUser(userId, false);
    }

    public Appointment cancel(String id, String userId) {
        Appointment appt = getById(id);
        if (!appt.getUserId().equals(userId)) {
            throw new BadRequestException("Not your appointment");
        }
        appt.setStatus(AppointmentStatus.CANCELLED);
        availabilityService.releaseSlot(appt.getDoctorId(), appt.getAppointmentDate(), appt.getAppointmentTime());
        return appointmentRepository.save(appt);
    }

    private List<Appointment> filterUser(String userId, boolean upcoming) {
        LocalDate today = LocalDate.now();
        return getUserAppointments(userId).stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> {
                    LocalDate d = LocalDate.parse(a.getAppointmentDate());
                    return upcoming ? !d.isBefore(today) : d.isBefore(today);
                })
                .sorted(Comparator.comparing(Appointment::getAppointmentDate))
                .collect(Collectors.toList());
    }
}
