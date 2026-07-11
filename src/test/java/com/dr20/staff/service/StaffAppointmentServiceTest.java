package com.dr20.staff.service;

import com.dr20.common.enums.AppointmentStatus;
import com.dr20.common.enums.UserRole;
import com.dr20.common.exception.ForbiddenException;
import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.AppointmentRepository;
import com.dr20.shared.repository.MedicalRecordRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.NotificationHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffAppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private MedicalRecordRepository medicalRecordRepository;
    @Mock private NotificationHelper notificationHelper;
    @InjectMocks private StaffAppointmentService staffAppointmentService;

    @Test
    void verifyToken_marksVerified() {
        User staff = staffUser();
        Appointment appt = appointment("d1", AppointmentStatus.CONFIRMED);

        when(userRepository.findById("s1")).thenReturn(Optional.of(staff));
        when(appointmentRepository.findByTokenNumber("7842")).thenReturn(Optional.of(appt));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = staffAppointmentService.verifyToken("s1", "7842");

        assertEquals(AppointmentStatus.VERIFIED, result.getStatus());
        assertNotNull(result.getVerifiedAt());
    }

    @Test
    void verifyToken_forbiddenForWrongDoctor() {
        User staff = staffUser();
        Appointment appt = appointment("other-doctor", AppointmentStatus.CONFIRMED);

        when(userRepository.findById("s1")).thenReturn(Optional.of(staff));
        when(appointmentRepository.findByTokenNumber("7842")).thenReturn(Optional.of(appt));

        assertThrows(ForbiddenException.class,
                () -> staffAppointmentService.verifyToken("s1", "7842"));
    }

    @Test
    void complete_requiresInProgress() {
        User staff = staffUser();
        Appointment appt = appointment("d1", AppointmentStatus.CONFIRMED);
        appt.setId("a1");

        when(userRepository.findById("s1")).thenReturn(Optional.of(staff));
        when(appointmentRepository.findById("a1")).thenReturn(Optional.of(appt));

        assertThrows(Exception.class,
                () -> staffAppointmentService.complete("s1", "a1", null));
    }

    private User staffUser() {
        User u = new User();
        u.setId("s1");
        u.setRole(UserRole.DOCTOR);
        u.setLinkedProfileId("d1");
        return u;
    }

    private Appointment appointment(String doctorId, AppointmentStatus status) {
        Appointment a = new Appointment();
        a.setDoctorId(doctorId);
        a.setStatus(status);
        a.setAppointmentDate("2026-07-15");
        return a;
    }
}
