package com.dr20.staff.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.AvailabilityBlock;
import com.dr20.shared.model.WorkingHoursSettings;
import com.dr20.shared.repository.AvailabilityBlockRepository;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.WorkingHoursSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAvailabilityService {

    private final WorkingHoursSettingsRepository settingsRepository;
    private final AvailabilityBlockRepository blockRepository;
    private final DoctorRepository doctorRepository;

    public WorkingHoursSettings getSettings(String doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return settingsRepository.findByDoctorId(doctorId)
                .orElseGet(() -> defaultSettings(doctorId));
    }

    public WorkingHoursSettings saveSettings(String doctorId, WorkingHoursSettings settings) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        WorkingHoursSettings existing = settingsRepository.findByDoctorId(doctorId)
                .orElse(new WorkingHoursSettings());
        existing.setDoctorId(doctorId);
        if (settings.getWorkingDays() != null) existing.setWorkingDays(settings.getWorkingDays());
        if (settings.getMorningStart() != null) existing.setMorningStart(settings.getMorningStart());
        if (settings.getMorningEnd() != null) existing.setMorningEnd(settings.getMorningEnd());
        if (settings.getEveningStart() != null) existing.setEveningStart(settings.getEveningStart());
        if (settings.getEveningEnd() != null) existing.setEveningEnd(settings.getEveningEnd());
        if (settings.getSlotDurationMinutes() != null) existing.setSlotDurationMinutes(settings.getSlotDurationMinutes());
        return settingsRepository.save(existing);
    }

    public AvailabilityBlock markUnavailable(String doctorId, String date, boolean fullDay, String halfDayPeriod) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        AvailabilityBlock block = blockRepository.findByDoctorIdAndDate(doctorId, date)
                .orElse(new AvailabilityBlock());
        block.setDoctorId(doctorId);
        block.setDate(date);
        block.setFullDay(fullDay);
        block.setHalfDayPeriod(halfDayPeriod);
        return blockRepository.save(block);
    }

    private WorkingHoursSettings defaultSettings(String doctorId) {
        WorkingHoursSettings s = new WorkingHoursSettings();
        s.setDoctorId(doctorId);
        s.setWorkingDays(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        s.setMorningStart("09:00 AM");
        s.setMorningEnd("01:00 PM");
        s.setEveningStart("06:00 PM");
        s.setEveningEnd("09:30 PM");
        s.setSlotDurationMinutes(30);
        return settingsRepository.save(s);
    }
}
