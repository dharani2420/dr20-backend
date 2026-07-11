# Dr.20 Patient App — Screen → API Map

Figma patient flow (4 screenshot sets). Staff/batch-1 work is **on hold** — this doc is patient-only.

**Live backend:** `https://dr20-backend.onrender.com`  
**Postman:** `postman/Dr20-Patient-E2E.postman_collection.json`  
**Testing guide:** `PATIENT_E2E_TESTING.md`

---

## Flow 1 — Splash, Onboarding, Auth, Profile

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Splash 33/34/35 | Logo, loading | — | Client only |
| Onboarding 111–113 | Next, Skip, Get Started | — | Client only |
| Login 115 | Phone +91, Send OTP | `POST /api/auth/send-otp` | **Done** |
| OTP 116 | 6-digit OTP, Resend, Verify | `POST /api/auth/verify-otp`, `resend-otp` | **Done** |
| Complete Profile 114 | First/Last name, Email, DOB, Gender, Blood Group, Create Profile | `POST /api/auth/complete-profile` | **Done** |
| Profile Created | Success modal | Uses step 3 response | Client only |

---

## Flow 2 — Home

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Home header | Hello {name}, profile, location Tambaram | `GET /api/home/{userId}` → `greeting`, `userName`, `location` | **Done** |
| Search bar | Search doctors/services/symptoms | `GET /api/search?q=` | **Done** |
| Hero banner | Healthcare at Your Doorstep | `banners` in home | **Done** |
| What Do You Need Today | Dr at ₹20, Home Healthcare | `consultations` in home | **Done** |
| Upcoming appointment card | Doctor, token, QR, clinic, View Appointment | `nextAppointment` in home | **Done** |
| Explore Care Services | Nursing, Elder, Physio, Lab | `careServices` in home | **Done** |
| Consult at ₹20 list | Doctor cards, Book now | `consultAt20Doctors` in home + `GET /api/doctors?maxFee=20` | **Done** |

---

## Flow 3 — Services, Doctors, Book, Pay

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Choose Specialization | 12 specialties grid | `GET /api/specializations` | **Done** |
| Services tab | Consultations, symptoms, care services | `GET /api/services` | **Done** (new) |
| Choose Doctors | Filters: All, ₹20, Nearest, Top Rated, Clinics | `GET /api/doctors` with `maxFee`, `clinicType`, `sort`, `latitude`, `longitude` | **Done** (enhanced) |
| Doctor Profile | About, expertise, date, slots, clinic, reviews | `GET /api/doctors/{id}/detail`, `/{id}/slots?date=` | **Done** |
| Booking Confirmation | Patient info, slot, bill (₹20, platform free) | `GET /api/payments/summary/{appointmentId}` | **Done** (enhanced) |
| Book | Confirm flow | `POST /api/appointments` | **Done** |
| Payment success | Green checkmark | `POST /api/payments/create-order` + `verify` (mock) | **Done** |
| Appointment Pass | QR, token #07, clinic map, Active Pass | `GET /api/appointments/{id}/pass` | **Done** (enhanced) |

---

## Flow 4 — Appointments tab & Profile

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Appointment list | Tabs: All, Completed, Upcoming, Cancelled | `GET .../user/{id}`, `/upcoming`, `/completed`, `/cancelled` | **Done** (`/completed` new) |
| Appointment Pass detail | Full pass view | `GET /api/appointments/{id}/pass` | **Done** |
| Profile | Name, phone, Edit Profile | `GET /api/auth/profile/{userId}` | **Done** |
| Family Members | Mother, Father, Add Member | `GET/POST /api/family/{userId}` | **Done** |
| Help, Terms, Privacy, About, Logout | Menu items | Static / client (logout = clear JWT) | Client only |

---

## Not in screenshots (existing code, not required for Android v1)

- Hospitals API
- Notifications list API (menu link only — can hardcode)
- Medical records
- Submit review
- Nurse/home-care provider booking (care icons shown; full nurse flow not in screenshots)

---

## Seed data (fresh MongoDB only)

| Item | Value |
|------|-------|
| Patient phone | `9876543210` |
| Patient name | pravin av |
| Doctor (₹20) | Dr. Priya Menon — Arokya Clinic |
| Upcoming token | `07`, QR `DR20-SEED001` |
| Staff phone (for QR verify) | `9123456781` |

**Note:** Seed runs only on empty DB. Existing Render DB keeps old data until redeploy + fresh DB or manual update.
