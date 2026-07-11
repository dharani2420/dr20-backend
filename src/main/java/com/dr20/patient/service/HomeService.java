package com.dr20.patient.service;

import com.dr20.shared.model.Appointment;
import com.dr20.shared.model.Banner;
import com.dr20.shared.model.ServiceCategory;
import com.dr20.shared.model.User;
import com.dr20.shared.repository.BannerRepository;
import com.dr20.shared.repository.ServiceCategoryRepository;
import com.dr20.shared.repository.UserRepository;
import com.dr20.shared.service.SpecializationService;
import com.dr20.shared.service.DoctorService;
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
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    public Map<String, Object> getHomeData(String userId) {
        List<Appointment> upcoming = appointmentService.getUpcoming(userId);
        User user = userRepository.findById(userId).orElse(null);

        Map<String, Object> data = new HashMap<>();
        if (user != null) {
            String name = user.getFirstName() != null
                    ? (user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "")).trim()
                    : "Guest";
            data.put("greeting", "Hello " + name);
            data.put("userName", name);
            data.put("profileImage", user.getProfileImage());
        }
        data.put("location", "Tambaram");
        data.put("banners", bannerRepository.findByActiveTrue());
        data.put("consultations", categoryRepository.findByType("CONSULTATION"));
        data.put("careServices", categoryRepository.findByType("CARE"));
        data.put("symptoms", categoryRepository.findByType("SYMPTOM"));
        data.put("specializations", specializationService.getAllSpecializations());
        data.put("consultAt20Doctors", doctorService.getConsultAt20Doctors());
        data.put("topDoctors", doctorService.getTopDoctors());
        data.put("nextAppointment", upcoming.isEmpty() ? null : upcoming.get(0));
        return data;
    }

    public Map<String, Object> getServicesScreen() {
        Map<String, Object> data = new HashMap<>();
        data.put("location", "Tambaram");
        data.put("consultations", categoryRepository.findByType("CONSULTATION"));
        data.put("symptoms", categoryRepository.findByType("SYMPTOM"));
        data.put("careServices", categoryRepository.findByType("CARE"));
        data.put("specializations", specializationService.getAllSpecializations());
        return data;
    }

    public List<ServiceCategory> getCategoriesByType(String type) {
        return categoryRepository.findByType(type.toUpperCase());
    }

    public List<Banner> getBanners() {
        return bannerRepository.findByActiveTrue();
    }
}
