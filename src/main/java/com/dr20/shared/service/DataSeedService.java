package com.dr20.shared.service;

import com.dr20.shared.model.*;
import com.dr20.common.enums.AppointmentStatus;
import com.dr20.common.enums.UserRole;
import com.dr20.shared.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSeedService {

    private final SpecializationRepository specializationRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final BannerRepository bannerRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityService availabilityService;

    @Value("${seed.admin.phone:9000000001}")
    private String adminPhone;

    @PostConstruct
    public void seed() {
        seedSpecializations();
        seedCategories();
        seedBanners();
        seedAdmin();
        seedDoctorsWithStaff();
        seedSampleAppointments();
    }

    private void seedAdmin() {
        if (userRepository.findByPhone(adminPhone).isPresent()) return;
        User admin = new User();
        admin.setPhone(adminPhone);
        admin.setFirstName("Admin");
        admin.setLastName("Dr20");
        admin.setRole(UserRole.ADMIN);
        admin.setProfession(UserRole.ADMIN);
        admin.setPhoneVerified(true);
        userRepository.save(admin);
        System.out.println("Seeded ADMIN phone: " + adminPhone);
    }

    private void seedDoctorsWithStaff() {
        if (doctorRepository.count() > 0) return;

        createStaffDoctor("9123456781", "Priya", "Menon", "DOCTOR", "General Practitioner", 600.0,
                "City Clinic", "45 Park Street, Mumbai");
        createStaffDoctor("9123456782", "Rajesh", "Kumar", "DOCTOR", "Cardiology", 800.0,
                "Apollo Clinic", "123 MG Road, Bangalore");
        createStaffDoctor("9123456783", "Amit", "Patel", "DOCTOR", "Neurology", 1200.0,
                "Neuro Care", "78 Ring Road, Delhi");
    }

    private void createStaffDoctor(String phone, String first, String last, String role,
                                     String specialization, double fee, String clinic, String address) {
        User user = new User();
        user.setPhone(phone);
        user.setFirstName(first);
        user.setLastName(last);
        user.setEmail(first.toLowerCase() + "@dr20.com");
        user.setRole(UserRole.valueOf(role));
        user.setProfession(UserRole.valueOf(role));
        user.setPhoneVerified(true);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUserId(user.getId());
        doctor.setName("Dr. " + first + " " + last);
        doctor.setDegree("MBBS");
        doctor.setSpecialization(specialization);
        doctor.setEmail(user.getEmail());
        doctor.setPhone(phone);
        doctor.setRating(4.8);
        doctor.setExperience(10);
        doctor.setConsultationFee(fee);
        doctor.setClinicName(clinic);
        doctor.setClinicAddress(address);
        doctor.setLatitude(19.0760);
        doctor.setLongitude(72.8777);
        doctor.setIsAvailable(true);
        doctorRepository.save(doctor);

        user.setLinkedProfileId(doctor.getId());
        userRepository.save(user);
        availabilityService.seedForDoctor(doctor.getId(), 14);

        System.out.println("Seeded staff doctor: " + phone + " / doctorId: " + doctor.getId());
    }

    private void seedSampleAppointments() {
        if (appointmentRepository.count() > 0) return;

        var doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) return;

        Doctor doctor = doctors.get(0);
        String today = LocalDate.now().toString();

        User patient = new User();
        patient.setPhone("9876543210");
        patient.setFirstName("Pravin");
        patient.setLastName("M");
        patient.setRole(UserRole.PATIENT);
        patient.setPhoneVerified(true);
        userRepository.save(patient);

        Appointment appt = new Appointment();
        appt.setUserId(patient.getId());
        appt.setDoctorId(doctor.getId());
        appt.setDoctorName(doctor.getName());
        appt.setSpecialization(doctor.getSpecialization());
        appt.setAppointmentDate(today);
        appt.setAppointmentTime("09:00 AM");
        appt.setConsultationType("In-Person");
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appt.setConsultationFee(doctor.getConsultationFee());
        appt.setPlatformFee(20.0);
        appt.setTotalFee(doctor.getConsultationFee() + 20.0);
        appt.setPaymentStatus("PAID");
        appt.setPatientName("Pravin M");
        appt.setSymptoms("General Consultation");
        appt.setQrData("DR20-SEED001");
        appt.setTokenNumber("7842");
        appointmentRepository.save(appt);

        availabilityService.bookSlot(doctor.getId(), today, "09:00 AM");

        System.out.println("Seeded sample appointment — token: 7842, qr: DR20-SEED001");
        System.out.println("Staff test phone: " + doctor.getPhone());
    }

    private void seedSpecializations() {
        if (specializationRepository.count() > 0) return;
        specializationRepository.saveAll(Arrays.asList(
            spec("Cardiology", "❤️", "Heart care"),
            spec("Dermatology", "🧴", "Skin care"),
            spec("Neurology", "🧠", "Brain care"),
            spec("General Practitioner", "🩺", "General medicine"),
            spec("Dental", "🦷", "Dental care")
        ));
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;
        categoryRepository.saveAll(Arrays.asList(
            cat("Go to Clinic", "🏥", "CONSULTATION"),
            cat("Home Healthcare", "🏠", "CONSULTATION"),
            cat("Nursing Care", "💉", "CARE"),
            cat("Lab Test", "🔬", "CARE"),
            cat("Fever", "🌡️", "SYMPTOM"),
            cat("Cough & Cold", "🤧", "SYMPTOM")
        ));
    }

    private void seedBanners() {
        if (bannerRepository.count() > 0) return;
        bannerRepository.save(new Banner(null, "Consult at ₹99", "Limited offer", "", true));
    }

    private Specialization spec(String name, String icon, String desc) {
        return new Specialization(null, name, icon, desc);
    }

    private ServiceCategory cat(String name, String icon, String type) {
        return new ServiceCategory(null, name, icon, type);
    }
}
