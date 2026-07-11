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
    private final NotificationRepository notificationRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final StaffVerificationRepository staffVerificationRepository;
    private final StaffDocumentRepository staffDocumentRepository;
    private final FamilyMemberRepository familyMemberRepository;

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
        seedFamilyMembers();
        seedNotificationsAndRecords();
        seedStaffVerification();
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

        createStaffDoctor("9123456781", "Priya", "Menon", "DOCTOR", "General Physician",
                20.0, "Arokya Clinic", "Tambaram, Chennai", "DR20_CLINIC", 4.8, 6, 1298);
        createStaffDoctor("9123456782", "Arun", "Kumar", "DOCTOR", "General Physician",
                20.0, "Nalam Clinic", "Tambaram, Chennai", "DR20_CLINIC", 4.7, 5, 1078);
        createStaffDoctor("9123456783", "Harini", "Balaji", "DOCTOR", "General Physician",
                300.0, "Private Care Clinic", "Tambaram, Chennai", "PRIVATE_CLINIC", 4.6, 8, 542);
    }

    private void createStaffDoctor(String phone, String first, String last, String role,
                                   String specialization, double fee, String clinic, String address,
                                   String clinicType, double rating, int experience, int reviewCount) {
        User user = new User();
        user.setPhone(phone);
        user.setFirstName(first);
        user.setLastName(last);
        user.setEmail(first.toLowerCase() + "@dr20.com");
        user.setRole(UserRole.valueOf(role));
        user.setProfession(UserRole.valueOf(role));
        user.setPhoneVerified(true);
        user.setGender("Priya".equals(first) || "Harini".equals(first) ? "Female" : "Male");
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUserId(user.getId());
        doctor.setName("Dr. " + first + " " + last);
        doctor.setDegree("MBBS");
        doctor.setSpecialization(specialization);
        doctor.setEmail(user.getEmail());
        doctor.setPhone(phone);
        doctor.setRating(rating);
        doctor.setExperience(experience);
        doctor.setReviewCount(reviewCount);
        doctor.setAbout("Experienced " + specialization.toLowerCase()
                + " providing trusted care at " + clinic + ".");
        doctor.setExpertise(Arrays.asList("Fever", "Cold & Cough", "Headache", "General Checkup"));
        doctor.setConsultationFee(fee);
        doctor.setClinicName(clinic);
        doctor.setClinicAddress(address);
        doctor.setClinicType(clinicType);
        doctor.setClinicHours("6:00 PM - 9:00 PM");
        doctor.setConsultationMode("In-person");
        doctor.setLatitude(12.9249);
        doctor.setLongitude(80.1000);
        doctor.setIsAvailable(true);
        doctor.setIsVerified(true);
        doctor.setIsClinicVerified(true);
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

        User patient = new User();
        patient.setPhone("9876543210");
        patient.setFirstName("pravin");
        patient.setLastName("av");
        patient.setEmail("pravin@dr20.com");
        patient.setDateOfBirth("1998-01-15");
        patient.setGender("Male");
        patient.setBloodGroup("O+");
        patient.setRole(UserRole.PATIENT);
        patient.setPhoneVerified(true);
        userRepository.save(patient);

        String tomorrow = LocalDate.now().plusDays(1).toString();

        Appointment appt = new Appointment();
        appt.setUserId(patient.getId());
        appt.setDoctorId(doctor.getId());
        appt.setDoctorName(doctor.getName());
        appt.setSpecialization(doctor.getSpecialization());
        appt.setAppointmentDate(tomorrow);
        appt.setAppointmentTime("09:30 AM");
        appt.setConsultationType("In-Person");
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appt.setConsultationFee(20.0);
        appt.setPlatformFee(0.0);
        appt.setTotalFee(20.0);
        appt.setPaymentStatus("PAID");
        appt.setPatientName("pravin av");
        appt.setPatientAge("26");
        appt.setPatientGender("Male");
        appt.setPatientBloodGroup("O+");
        appt.setSymptoms("General Consultation");
        appt.setQrData("DR20-SEED001");
        appt.setTokenNumber("07");
        appointmentRepository.save(appt);

        availabilityService.bookSlot(doctor.getId(), tomorrow, "09:30 AM");

        Doctor harini = doctors.stream()
                .filter(d -> d.getName() != null && d.getName().contains("Harini"))
                .findFirst().orElse(doctor);
        Appointment completed = new Appointment();
        completed.setUserId(patient.getId());
        completed.setDoctorId(harini.getId());
        completed.setDoctorName(harini.getName());
        completed.setSpecialization(harini.getSpecialization());
        completed.setAppointmentDate(LocalDate.now().minusDays(1).toString());
        completed.setAppointmentTime("10:00 AM");
        completed.setConsultationType("In-Person");
        completed.setStatus(AppointmentStatus.COMPLETED);
        completed.setConsultationFee(300.0);
        completed.setPlatformFee(20.0);
        completed.setTotalFee(320.0);
        completed.setPaymentStatus("PAID");
        completed.setPatientName("pravin av");
        completed.setPatientAge("26");
        completed.setPatientGender("Male");
        completed.setPatientBloodGroup("O+");
        completed.setQrData("DR20-COMPLETED01");
        completed.setTokenNumber("07");
        appointmentRepository.save(completed);

        System.out.println("Seeded sample appointment — token: 07, qr: DR20-SEED001");
        System.out.println("Patient test phone: 9876543210");
        System.out.println("Staff test phone: " + doctor.getPhone());
    }

    private void seedFamilyMembers() {
        if (familyMemberRepository.count() > 0) return;
        userRepository.findByPhone("9876543210").ifPresent(patient -> {
            FamilyMember mother = new FamilyMember();
            mother.setUserId(patient.getId());
            mother.setName("Mother");
            mother.setRelation("Mother");
            mother.setAge(52);
            mother.setGender("Female");
            familyMemberRepository.save(mother);

            FamilyMember father = new FamilyMember();
            father.setUserId(patient.getId());
            father.setName("Father");
            father.setRelation("Father");
            father.setAge(55);
            father.setGender("Male");
            familyMemberRepository.save(father);
        });
    }

    private void seedSpecializations() {
        if (specializationRepository.count() > 0) return;
        specializationRepository.saveAll(Arrays.asList(
            spec("Cardiology", "❤️", "Heart care"),
            spec("Dermatology", "🧴", "Skin care"),
            spec("Neurology", "🧠", "Brain care"),
            spec("General Practitioner", "🩺", "General medicine"),
            spec("Dental", "🦷", "Dental care"),
            spec("Ophthalmology", "👁️", "Eye care")
        ));
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) return;
        categoryRepository.saveAll(Arrays.asList(
            cat("Dr at ₹20", "🩺", "CONSULTATION"),
            cat("Private Clinics", "🏥", "CONSULTATION"),
            cat("Home Healthcare", "🏠", "CONSULTATION"),
            cat("Nursing Care", "💉", "CARE"),
            cat("Elder Care", "👴", "CARE"),
            cat("Physiotherapy", "🦴", "CARE"),
            cat("Lab Tests", "🔬", "CARE"),
            cat("General Physician", "🩺", "SYMPTOM"),
            cat("Fever", "🌡️", "SYMPTOM"),
            cat("Cough & Cold", "🤧", "SYMPTOM")
        ));
    }

    private void seedBanners() {
        if (bannerRepository.count() > 0) return;
        bannerRepository.save(new Banner(null, "Healthcare at Your Doorstep",
                "Consult doctors and book lab tests from home", "", true));
        bannerRepository.save(new Banner(null, "Consult Certified Doctors at Just ₹20",
                "Trusted doctors available near you", "", true));
    }

    private void seedNotificationsAndRecords() {
        if (notificationRepository.count() > 0) return;

        userRepository.findByPhone("9876543210").ifPresent(patient -> {
            Notification n1 = new Notification();
            n1.setUserId(patient.getId());
            n1.setTitle("Appointment Reminder");
            n1.setMessage("Your consultation with Dr. Priya Menon is today at 09:00 AM");
            n1.setType("APPOINTMENT");
            notificationRepository.save(n1);

            Notification n2 = new Notification();
            n2.setUserId(patient.getId());
            n2.setTitle("Welcome to HEALTO");
            n2.setMessage("Complete your profile to get personalized care recommendations");
            n2.setType("GENERAL");
            notificationRepository.save(n2);

            MedicalRecord record = new MedicalRecord();
            record.setUserId(patient.getId());
            record.setTitle("General Consultation Summary");
            record.setType("VISIT_SUMMARY");
            record.setDoctorName("Dr. Priya Menon");
            record.setNotes("Routine checkup — vitals normal");
            record.setFileUrl("https://example.com/records/sample-summary.pdf");
            medicalRecordRepository.save(record);
        });
    }

    private void seedStaffVerification() {
        if (staffVerificationRepository.count() > 0) return;
        userRepository.findByPhone("9123456781").ifPresent(staff -> {
            staff.setVerificationStatus("APPROVED");
            userRepository.save(staff);

            StaffVerification v = new StaffVerification();
            v.setUserId(staff.getId());
            v.setOverallStatus("APPROVED");
            v.setPersonalInfoStatus("COMPLETED");
            v.setProfessionalInfoStatus("COMPLETED");
            v.setDocumentsStatus("COMPLETED");
            v.setAdminReviewStatus("COMPLETED");
            staffVerificationRepository.save(v);

            staffDocumentRepository.save(new StaffDocument(null, staff.getId(), "IDENTITY",
                    "Identity Proof (Aadhaar)", "https://example.com/docs/aadhaar.pdf", "VERIFIED", java.time.LocalDateTime.now()));
            staffDocumentRepository.save(new StaffDocument(null, staff.getId(), "PROFESSIONAL_CERT",
                    "Medical Registration Certificate", "https://example.com/docs/mrc.pdf", "VERIFIED", java.time.LocalDateTime.now()));
            staffDocumentRepository.save(new StaffDocument(null, staff.getId(), "PROFILE_PHOTO",
                    "Profile Photo", "https://example.com/docs/photo.jpg", "VERIFIED", java.time.LocalDateTime.now()));
        });
    }

    private Specialization spec(String name, String icon, String desc) {
        return new Specialization(null, name, icon, desc);
    }

    private ServiceCategory cat(String name, String icon, String type) {
        return new ServiceCategory(null, name, icon, type);
    }
}
