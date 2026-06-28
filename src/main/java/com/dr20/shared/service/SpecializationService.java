package com.dr20.shared.service;

import com.dr20.shared.model.Specialization;
import com.dr20.shared.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public Specialization addSpecialization(Specialization spec) {
        return specializationRepository.save(spec);
    }
}
