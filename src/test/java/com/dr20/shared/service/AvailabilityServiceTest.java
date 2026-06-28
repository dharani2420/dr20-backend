package com.dr20.shared.service;

import com.dr20.common.exception.BadRequestException;
import com.dr20.shared.model.Availability;
import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.TimeSlot;
import com.dr20.shared.repository.AvailabilityRepository;
import com.dr20.shared.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock private AvailabilityRepository availabilityRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private MongoTemplate mongoTemplate;
    @InjectMocks private AvailabilityService availabilityService;

    @Test
    void getAvailableSlots_rejectsPastDate() {
        assertThrows(BadRequestException.class,
                () -> availabilityService.getAvailableSlots("d1", "2020-01-01"));
    }

    @Test
    void getAvailableSlots_returnsFreeSlots() {
        String today = LocalDate.now().toString();
        Doctor doctor = new Doctor();
        doctor.setId("d1");

        Availability avail = new Availability();
        avail.setSlots(new ArrayList<>(List.of(
                new TimeSlot("09:00 AM", false),
                new TimeSlot("10:00 AM", true))));

        when(doctorRepository.findById("d1")).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctorIdAndDate("d1", today)).thenReturn(Optional.of(avail));

        List<String> result = availabilityService.getAvailableSlots("d1", today);
        assertEquals(1, result.size());
    }
}
