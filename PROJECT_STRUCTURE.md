# Dr.20 Project Structure

```
src/main/java/com/dr20/
├── Dr20Application.java          ← Start here
│
├── patient/                      ← PATIENT APP (Figma patient screens)
│   ├── controller/
│   │   ├── AuthController.java       /api/auth/*
│   │   ├── HomeController.java       /api/home/*, /api/search
│   │   ├── DoctorController.java     /api/doctors/*
│   │   ├── AppointmentController.java
│   │   ├── PaymentController.java
│   │   ├── FamilyController.java
│   │   └── SpecializationController.java
│   └── service/
│       ├── AuthService.java
│       ├── HomeService.java
│       ├── SearchService.java
│       ├── AppointmentService.java
│       ├── PaymentService.java
│       └── FamilyService.java
│
├── staff/                        ← STAFF APP (Figma provider screens)
│   ├── controller/
│   │   ├── StaffAuthController.java  /api/staff/auth/*
│   │   └── StaffController.java      /api/staff/*
│   └── service/
│       ├── StaffAuthService.java
│       └── StaffAppointmentService.java
│
├── admin/                        ← Admin APIs
│   ├── controller/AdminController.java
│   └── service/AdminService.java
│
├── shared/                       ← Used by both patient & staff
│   ├── model/                    User, Doctor, Appointment, Availability...
│   ├── repository/
│   └── service/
│       ├── OtpService.java
│       ├── AvailabilityService.java
│       ├── DoctorService.java
│       ├── DataSeedService.java
│       ├── sms/
│       └── payment/
│
└── common/                       ← Config, security, enums
    ├── config/AppConstants.java
    ├── enums/UserRole.java, AppointmentStatus.java
    ├── exception/
    └── security/JwtUtil, SecurityConfig...
```

## Quick find

| I need... | Go to folder |
|-----------|--------------|
| Patient login / profile | `patient/` |
| Staff login / verify QR | `staff/` |
| Doctor slots / booking logic | `shared/service/AvailabilityService` |
| Database models | `shared/model/` |
| JWT / security | `common/security/` |

See **TESTING.md** for API test steps.
