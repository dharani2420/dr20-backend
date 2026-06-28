package com.dr20.patient.service;

import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Banner;
import com.dr20.shared.model.ServiceCategory;
import com.dr20.shared.repository.BannerRepository;
import com.dr20.shared.repository.ServiceCategoryRepository;
import com.dr20.shared.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final BannerRepository bannerRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final SpecializationService specializationService;
    private final AppointmentService appointmentService;

    public Map<String, Object> getHomeData(String userId) {
        List<Appointment> upcoming = appointmentService.getUpcoming(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("banners", bannerRepository.findByActiveTrue());
        data.put("consultations", categoryRepository.findByType("CONSULTATION"));
        data.put("careServices", categoryRepository.findByType("CARE"));
        data.put("symptoms", categoryRepository.findByType("SYMPTOM"));
        data.put("specializations", specializationService.getAllSpecializations());
        data.put("nextAppointment", upcoming.isEmpty() ? null : upcoming.get(0));
        return data;
    }

    public List<ServiceCategory> getCategoriesByType(String type) {
        return categoryRepository.findByType(type.toUpperCase());
    }

    public List<Banner> getBanners() {
        return bannerRepository.findByActiveTrue();
    }
}
