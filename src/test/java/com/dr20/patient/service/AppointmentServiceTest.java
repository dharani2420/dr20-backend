package com.dr20.patient.service;

import com.dr20.common.enums.AppointmentStatus;
import com.dr20.common.exception.BadRequestException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.FamilyMemberRepository;
import com.dr20.shared.service.AvailabilityService;
import com.dr20.shared.service.NotificationHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private AvailabilityService availabilityService;
    @Mock private FamilyMemberRepository familyMemberRepository;
    @Mock private NotificationHelper notificationHelper;
    @InjectMocks private AppointmentService appointmentService;

    @Test
    void book_success() {
        Doctor doctor = new Doctor();
        doctor.setId("d1");
        doctor.setConsultationFee(600.0);

        Appointment req = new Appointment();
        req.setDoctorId("d1");
        req.setUserId("u1");
        req.setAppointmentDate("2026-07-15");
        req.setAppointmentTime("09:00 AM");

        when(doctorRepository.findById("d1")).thenReturn(Optional.of(doctor));
        when(availabilityService.bookSlot("d1", "2026-07-15", "09:00 AM")).thenReturn(true);
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.book(req);

        assertEquals(AppointmentStatus.UPCOMING, result.getStatus());
        assertEquals(620.0, result.getTotalFee());
    }

    @Test
    void book_failsWhenSlotTaken() {
        Doctor doctor = new Doctor();
        doctor.setId("d1");
        Appointment req = new Appointment();
        req.setDoctorId("d1");
        req.setAppointmentDate("2026-07-15");
        req.setAppointmentTime("09:00 AM");

        when(doctorRepository.findById("d1")).thenReturn(Optional.of(doctor));
        when(availabilityService.bookSlot(anyString(), anyString(), anyString())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> appointmentService.book(req));
    }
}
