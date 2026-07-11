# Dr.20 Figma → Backend Audit (Batch 1)

Figma: [Dr.20 Design](https://www.figma.com/design/Mhuuy5puhqRfhlZCVeW0GY/Dr.20?node-id=0-1)

**Batch 1 scope:** Staff app (majority) + Doctor/Nurse listing (patient discovery).  
**Batch 2:** Pending — patient auth/home/booking screens not fully in batch 1.

**Rules applied:**
- OTP + mock payment kept (staging)
- Client-only screens = no API
- EXTRA APIs **not deleted** (per your request — may break Render). Documented below; removed from Postman batch 1.

---

## Batch 1 — Screen audit table

| # | Screen | UI fields / buttons | Required API | Status |
|---|--------|---------------------|--------------|--------|
| **STAFF — Onboarding (client-only)** |
| S1 | Splash | Logo, loading | — | Client only |
| S2 | Onboarding 1 Welcome | Next, Skip | — | Client only |
| S3 | Onboarding 2 Schedule | Next, Back | — | Client only |
| S4 | Onboarding 3 Verified | Get Started | — | Client only |
| S5 | Choose Profession | Doctor/Nurse/Physio/Lab/Elder Care radio, Continue | `POST /api/staff/auth/register` (profession) | **Done** |
| **STAFF — Auth** |
| S6 | Welcome Doctor Login | Phone +91, Continue, Create Account | `POST /api/staff/auth/send-otp` | **Done** |
| S7 | OTP verify | OTP input | `POST /api/staff/auth/verify-otp` | **Done** |
| S8 | Resend OTP | Resend | `POST /api/staff/auth/resend-otp` | **Done** |
| **STAFF — Registration (5 steps)** |
| S9 | Complete Profile (1/5) | Name, email, gender, DOB | `PUT /api/staff/profile` | **Done** (partial — extend fields) |
| S10 | Professional Info (2/5) | Specialization, experience, qualification, reg#, languages | `PUT /api/staff/profile` + Doctor fields | **Done** (partial) |
| S11 | Service Info (3/5) | Clinic type, name, address, consultation mode, travel radius | Doctor model fields | **Done** (partial) |
| S12 | Verification Docs (4/5) | Upload MRC, ID, photo, bank details | `GET /api/staff/documents` + upload (file URL) | **Done** list; upload **Missing** (needs S3) |
| S13 | Review & Submit (5/5) | Confirm checkbox, Submit Profile | `POST /api/staff/profile/submit` | **Done** |
| S14 | Profile Submitted | Success message | Uses submit response | **Done** |
| S15 | Verification in Progress | 4-step checklist, Refresh | `GET /api/staff/verification-status` | **Done** |
| S16 | Verification Approved | Welcome message | verification status = APPROVED | **Done** |
| **STAFF — Home & Bookings** |
| S17 | Dashboard / Home | Today overview, upcoming list, notification bell | `GET /api/staff/dashboard` | **Done** |
| S18 | Bookings | Search, Upcoming/Completed tabs | `GET .../upcoming`, `.../past`, `.../search?q=` | **Done** |
| S19 | Appointment Details | Date, time, token, patient gender/age/blood, QR | `GET /api/staff/appointments/{id}/detail` | **Done** |
| S20 | Home visit details | Service address, map, Navigate | detail includes `serviceAddress` | **Done** (field on appointment) |
| S21 | Navigate to Patient | Map, Open Google Maps | Client + address from detail API | **Done** |
| S22 | I've Arrived | Arrival button | `POST /api/staff/appointments/{id}/arrive` | **Done** |
| S23 | Arrived Confirmed | Arrival time auto-recorded | arrive response `arrivedAt` | **Done** |
| S24 | Scan QR Code | Camera, Verify with Token | `POST /api/staff/appointments/verify-qr` | **Done** |
| S25 | Verify with Token modal | Token input, Verify | `POST /api/staff/appointments/verify-token` | **Done** |
| S26 | Patient Verified | Start Consultation | `POST /api/staff/appointments/{id}/start` | **Done** |
| S27 | In Progress | Complete Appointment | `POST /api/staff/appointments/{id}/complete` | **Done** |
| S28 | Complete modal | Cancel / Complete confirm | same complete API | **Done** |
| S29 | Appointment Completed | Back to Home | — | Client only |
| **STAFF — Schedule** |
| S30 | Schedule calendar | Working hours, slots grid, Manage Availability | `GET /api/staff/schedule?from&to` | **Done** |
| S31 | Working Hours | Days, morning/evening sessions, slot duration | `GET/PUT /api/staff/availability/settings` | **Done** |
| S32 | Mark Unavailable | Date, Full/Half day, Save | `POST /api/staff/availability/block` | **Done** |
| **STAFF — Profile** |
| S33 | Profile menu | Personal, Professional, Docs, Earnings, Help, Logout | `GET /api/staff/profile` | **Done** |
| S34 | Personal Information | Photo, name, mobile, email, DOB, gender, address | `GET/PUT /api/staff/profile` | **Done** |
| S35 | Professional Information | Specialization, experience, languages, clinic, reg# | `GET/PUT /api/staff/profile` | **Done** |
| S36 | Verification Documents | Doc list, bank details, verified badges | `GET /api/staff/documents` | **Done** |
| S37 | Earnings | Total, today, month, recent list | `GET /api/staff/earnings/summary`, `/transactions` | **Done** |
| S38 | Help & Support | FAQ, phone, email | Static / client | Client only |
| S39 | Terms & Conditions | Legal text | Static / client | Client only |
| S40 | Privacy Policy | Legal text | Static / client | Client only |
| S41 | About Dr.20 | App info | Static / client | Client only |
| S42 | Logout modal | Cancel / Logout | Client clears JWT | Client only |
| **PATIENT — Discovery (batch 1 only)** |
| P1 | Doctor Search & Listing | Search, filters (gender, rating, availability), clinic cards | `GET /api/doctors?gender&minRating&availableToday` | **Done** |
| P2 | Doctor Profile button | View doctor | `GET /api/doctors/{id}` | **Done** |
| P3 | Book now | Book CTA | Booking flow — **Batch 2** | Pending batch 2 |
| P4 | Nurse / Book Care listing | Nurse cards, starting price | Separate nurse provider API | **Missing** (batch 2?) |

---

## EXTRA APIs (in code, NOT in batch 1 Figma)

**Not deleted** — confirm before removing from production.

| API group | Why EXTRA (batch 1) |
|-----------|---------------------|
| `GET /api/hospitals/**` | No hospital directory screen in batch 1 |
| `GET /api/banners`, `/api/categories` | Not in batch 1 staff screens |
| `GET/POST /api/notifications/**` | Bell icon only; no notifications list screen in batch 1 |
| `GET/POST /api/medical-records/**` | Not in batch 1 |
| `POST/GET /api/reviews/**` | Reviews shown on doctor card but no submit-review screen in batch 1 |
| `GET /api/doctors/top` | Not explicitly in batch 1 listing screen |
| `GET /api/doctors/{id}/detail` | Overlaps with `GET /api/doctors/{id}` |
| Patient auth/home/family/payment | **Wait for batch 2** patient screenshots |

---

## Postman collections (batch 1 aligned)

### Staff — `postman/Dr20-Staff-Phase1.postman_collection.json`
Covers S5–S42 staff flow.

### Patient — trimmed for batch 1 only
- Keep: `GET /api/doctors` (with filters), `GET /api/doctors/{id}`, `GET /api/doctors/{id}/slots`
- Removed from Postman batch 1: hospitals, notifications, medical records, reviews, banners, categories, full auth/booking (batch 2)

---

## Final checklist (batch 1)

| Figma screen | Postman step | API | Tested |
|--------------|--------------|-----|--------|
| S6 Login | 1. Staff Send OTP | `POST /api/staff/auth/send-otp` | Y/N |
| S7 OTP | 2. Staff Verify OTP | `POST /api/staff/auth/verify-otp` | Y/N |
| S17 Dashboard | 3. Staff Dashboard | `GET /api/staff/dashboard` | Y/N |
| S18 Bookings | 4. Upcoming / 4b Past / 4d Search | `GET .../upcoming`, `/past`, `/search` | Y/N |
| S19 Details | 4e + 4f Detail | `GET .../{id}`, `.../{id}/detail` | Y/N |
| S24-S25 Verify | 5 Token / 6 QR | verify-token, verify-qr | Y/N |
| S26 Start | 7. Start | `POST .../{id}/start` | Y/N |
| S27 Complete | 8. Complete | `POST .../{id}/complete` | Y/N |
| S22 Arrive | 8b. Arrive | `POST .../{id}/arrive` | Y/N |
| S31 Working Hours | 14-15 | `GET/PUT /api/staff/availability/settings` | Y/N |
| S32 Mark Unavailable | 16 | `POST /api/staff/availability/block` | Y/N |
| S15 Verification | 17 | `GET /api/staff/verification-status` | Y/N |
| S37 Earnings | 18-19 | `GET /api/staff/earnings/summary`, `/transactions` | Y/N |
| S36 Documents | 20 | `GET /api/staff/documents` | Y/N |
| P1 Doctor list | P1 List Doctors | `GET /api/doctors?minRating=&availableToday=` | Y/N |

---

## Next: Batch 2

When you send remaining patient screenshots, we will audit:
- Patient auth (OTP, profile)
- Home, search, categories
- Full booking + payment + pass
- Family members
- Confirm which EXTRA APIs to keep or remove
