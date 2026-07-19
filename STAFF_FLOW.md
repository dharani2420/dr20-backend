# Dr.20 Staff App — Screen → API Map

Figma staff flow (all batches shared). Patient app: `PATIENT_FLOW.md`.

**Live backend:** `https://dr20-backend.onrender.com`  
**Postman:** `postman/Dr20-Staff-E2E.postman_collection.json`  
**Testing guide:** `STAFF_E2E_TESTING.md`

---

## Flow 1 — Splash, Onboarding, Auth, Registration

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Splash | Logo, loading | — | Client only |
| Onboarding 1–3 | Next, Skip, Get Started | — | Client only |
| Choose Profession | Doctor/Nurse/Physio/Lab/Elder Care | `POST /api/staff/auth/register` | **Done** |
| Welcome Login | Phone +91, Continue | `POST /api/staff/auth/send-otp` | **Done** |
| OTP verify / Resend | OTP input | `verify-otp`, `resend-otp` | **Done** |
| Complete Profile (1/5) | Name, email, gender, DOB | `PUT /api/staff/profile` | **Done** |
| Professional Info (2/5) | Specialization, experience, languages | `GET/PUT /api/staff/profile/professional` | **Done** (updated) |
| Service Info (3/5) | Clinic type, name, address, mode | `PUT /api/staff/profile/professional` | **Done** (updated) |
| Verification Docs (4/5) | Upload docs + bank | `POST /api/staff/documents`, `POST /api/staff/documents/bank` | **Done** (staging: fileUrl) |
| Review & Submit (5/5) | Submit Profile | `POST /api/staff/profile/submit` | **Done** |
| Profile Submitted | Success modal | submit response | Client only |
| Verification in Progress | 4-step checklist | `GET /api/staff/verification-status` | **Done** |
| Verification Approved | Welcome | `overallStatus: APPROVED` | **Done** |

---

## Flow 2 — Dashboard & Bookings

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Home header | Dr name, specialization, bell | `GET /api/staff/dashboard` | **Done** |
| Today's Overview | Today count, Completed count | `todayOverview.todayAppointments`, `completedToday` | **Done** |
| Upcoming cards | Patient, service, token, relative time | `upcomingAppointments[]` | **Done** |
| Bookings tabs | Upcoming / Completed counts | `GET /api/staff/appointments/counts` | **Done** |
| Bookings list | Upcoming / Completed | `/upcoming`, `/past` | **Done** |
| Search | Patient name or token | `GET /api/staff/appointments/search?q=` | **Done** |
| No Appointment Today | Empty state | Empty list from dashboard | Client only |

---

## Flow 3 — Appointment detail, verify, consult (clinic)

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Appointment Details | Patient info, mobile, reason, token | `GET /api/staff/appointments/{id}/detail` | **Done** |
| Scan QR / Verify Token | QR or token modal | `verify-qr`, `verify-token` | **Done** |
| Patient Verified | Start Consultation | `POST .../{id}/start` | **Done** |
| In Progress | Complete Appointment | `POST .../{id}/complete` | **Done** |
| Appointment Completed | Back to Home | complete response | Client only |

---

## Flow 4 — Home visit (nurse / home nursing)

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Appointment Details | Service address, map, Navigate | `GET .../detail` → `serviceAddress` | **Done** |
| Navigate to Patient | Address, mobile, maps link | `GET /api/staff/appointments/{id}/navigate` | **Done** (new) |
| I've Arrived | Arrival button | `POST /api/staff/appointments/{id}/arrive` | **Done** (updated) |
| Arrival Confirmed | Same + verification section | arrive → `arrivalConfirmed: true` | **Done** (updated) |
| Scan QR / Verify Token | Same as clinic | verify APIs | **Done** |
| Service In Progress | Complete Service | `POST .../{id}/complete` | **Done** |
| Service Completed | Back to Home | Client only | Client only |

---

## Flow 5 — Schedule & availability

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Schedule calendar | Month, working hours, slots | `GET /api/staff/schedule/day?date=` | **Done** |
| Manage Availability | Working Hours, Mark Unavailable | Client navigation | Client only |
| Working Hours form | Days, sessions, slot duration | `GET/PUT /api/staff/availability/settings` | **Done** |
| Mark Unavailable | Full / half day | `POST /api/staff/availability/block` | **Done** |

---

## Flow 6 — Profile, documents, earnings

| Screen | UI | API | Status |
|--------|-----|-----|--------|
| Profile menu | Name, specialty, Verified badge | `GET /api/staff/profile/summary` | **Done** (new) |
| Personal Information | Name, email, gender, DOB, address, photo | `GET /api/staff/profile/personal`, `PUT /api/staff/profile`, `POST /api/staff/profile/photo` | **Done** (updated) |
| Professional Information | Category/specialization (read-only), experience, languages, clinic, reg# | `GET/PUT /api/staff/profile/professional` | **Done** (new) |
| Verification Documents | Docs list + bank + payout info | `GET /api/staff/documents` | **Done** (updated) |
| Upload document | File picker | `POST /api/staff/documents` `{ type, title, fileUrl }` | **Done** (staging URL) |
| Bank details | Bank name, masked account | `POST /api/staff/documents/bank` | **Done** (new) |
| Earnings | Total, today, month, completed count | `GET /api/staff/earnings/summary` | **Done** (updated) |
| Recent earnings list | Patient, type, amount, date | `GET /api/staff/earnings/transactions` | **Done** (updated) |
| Help & Support | FAQ, phone, email | Static | Client only |
| Terms & Conditions | Legal text | Static | Client only |
| Privacy Policy | Legal text | Static | Client only |
| About Dr.20 | App info | Static | Client only |
| Logout | Clear JWT | Client only | Client only |
| No Internet | Error state | Client only | Client only |

---

## Key response fields

| Field | Endpoint | Purpose |
|-------|----------|---------|
| `cardLabel`, `relativeTime` | dashboard, bookings | Figma appointment cards |
| `patientMobile`, `reasonForVisit` | detail | Appointment detail screen |
| `serviceAddress.distanceKm`, `travelMinutes` | detail, navigate | Home visit navigation |
| `googleMapsUrl` | navigate, arrive | Open in Google Maps |
| `arrivalConfirmed` | arrive | Arrival Confirmed screen |
| `isVerified`, `verificationBadge` | profile/summary | Profile Verified badge |
| `thisMonth` | earnings/summary | Earnings screen |
| `displayDate` | earnings/transactions | "Today - 10:30 AM" |
| `bankDetails`, `automaticPayout` | documents | Verification Documents screen |

---

## Seed data (fresh MongoDB only)

| Item | Value |
|------|-------|
| Staff phone | `9123456781` (Dr. Priya Menon) |
| Clinic | Dr.20 Tambaram Clinic |
| Bank | HDFC Bank, `XXXX XXXX 643` |
| Verify token / QR | `07` / `DR20-SEED001` |
| Languages | Tamil, English |

---

## Still optional / future

- Real file upload (S3) instead of `fileUrl` in staging
- Staff notifications list API
- Production Razorpay payout integration
