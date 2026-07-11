package com.dr20.patient.service;

import com.dr20.common.exception.ResourceNotFoundException;
import com.dr20.shared.model.MedicalRecord;
import com.dr20.shared.repository.MedicalRecordRepository;
import com.dr20.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    public List<MedicalRecord> getByUser(String userId) {
        validateUser(userId);
        return medicalRecordRepository.findByUserIdOrderByRecordDateDesc(userId);
    }

    public MedicalRecord getById(String id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found"));
    }

    public MedicalRecord add(String userId, MedicalRecord record) {
        validateUser(userId);
        record.setUserId(userId);
        return medicalRecordRepository.save(record);
    }

    private void validateUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
