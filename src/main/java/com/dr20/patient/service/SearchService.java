package com.dr20.patient.service;

import com.dr20.shared.model.Doctor;
import com.dr20.shared.model.ServiceCategory;
import com.dr20.shared.model.Specialization;
import com.dr20.shared.repository.DoctorRepository;
import com.dr20.shared.repository.ServiceCategoryRepository;
import com.dr20.shared.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final ServiceCategoryRepository categoryRepository;

    public Map<String, Object> search(String q) {
        var results = new LinkedHashSet<Doctor>();
        results.addAll(doctorRepository.findByNameContainingIgnoreCase(q));
        results.addAll(doctorRepository.findBySpecializationContainingIgnoreCase(q));

        List<Specialization> specs = specializationRepository.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());

        List<ServiceCategory> categories = categoryRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(q.toLowerCase()))
                .collect(Collectors.toList());

        Map<String, Object> res = new HashMap<>();
        res.put("doctors", new ArrayList<>(results));
        res.put("specializations", specs);
        res.put("symptoms", categories.stream().filter(c -> "SYMPTOM".equals(c.getType())).collect(Collectors.toList()));
        return res;
    }
}
