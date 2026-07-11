# Dr.20 Patient E2E — Testing Guide

**Base URL:** `https://dr20-backend.onrender.com`  
**Postman:** Import `postman/Dr20-Patient-E2E.postman_collection.json`

---

## Before you start

1. **Cold start:** Render free tier may take 30–60s on first request.
2. **Auth:** Postman Auth tab = **No Auth**. Protected requests use one header: `Authorization: Bearer {{patientToken}}`.
3. **OTP:** After Send OTP, copy OTP from **Render Logs** (staging prints OTP in console).
4. **Two test paths:**
   - **Path A — Seed patient:** phone `9876543210` (profile already complete on fresh DB)
   - **Path B — New signup:** use step `1b` with `9444444444`, then steps 2 → 3

---

## End-to-end test sequence

Run Postman steps **in order**. Check ✅ expected result.

### Auth & Profile

| Step | Postman | API | Expected |
|------|---------|-----|----------|
| 1 | 1. Send OTP | `POST /api/auth/send-otp` | `success: true` |
| 2 | 2. Verify OTP | `POST /api/auth/verify-otp` | `token`, `userId`, `isProfileComplete` saved to variables |
| 3 | 3. Complete Profile | `POST /api/auth/complete-profile` | Only if `isProfileComplete: false`. Returns `user` with name, email, DOB, gender, bloodGroup |

**Android screens:** Login → OTP → Complete Profile → Profile Created

---

### Home & Discovery

| Step | Postman | API | Expected |
|------|---------|-----|----------|
| 4 | 4. Home Screen | `GET /api/home/{userId}` | `greeting`, `location`, `banners`, `consultAt20Doctors`, `nextAppointment` with QR/token |
| 5 | 5. Services Screen | `GET /api/services` | `consultations`, `symptoms`, `careServices`, `specializations` |
| 6 | 6. Search | `GET /api/search?q=Priya` | `doctors`, `specializations`, `symptoms` |
| 7 | 7. Specializations | `GET /api/specializations` | List with icons (Choose Specialization modal) |
| 8 | 8. List Doctors ₹20 | `GET /api/doctors?maxFee=20&clinicType=DR20_CLINIC` | Doctors with `consultationFee: 20`, saves `doctorId` |
| 8b | 8b. Private Clinics | `?clinicType=PRIVATE_CLINIC` | Higher fee doctors (e.g. ₹300) |
| 8c | 8c. Nearest | `?sort=nearest&latitude=12.9249&longitude=80.1000` | `distanceKm` on each doctor |

**Android screens:** Home → Services tab → Choose Doctors (filters)

---

### Doctor Profile & Booking

| Step | Postman | API | Expected |
|------|---------|-----|----------|
| 9 | 9. Doctor Profile | `GET /api/doctors/{id}/detail` | `doctor` (about, expertise, clinic), `reviews`, `averageRating` |
| 10 | 10. Doctor Slots | `GET /api/doctors/{id}/slots?date=` | Array of time strings — pick one for `appointmentTime` |
| 11 | 11. Book Appointment | `POST /api/appointments` | `status: UPCOMING`, `paymentStatus: PENDING`, `tokenNumber`, `qrData`, `totalFee: 20` for ₹20 doctors |
| 12 | 12. Bill Summary | `GET /api/payments/summary/{appointmentId}` | `itemTotal: 20`, `platformFeeWaived: true`, `totalPayable: 20` |

**Android screens:** Doctor Profile → Booking Confirmation

---

### Payment & Pass

| Step | Postman | API | Expected |
|------|---------|-----|----------|
| 13 | 13. Create Payment Order | `POST /api/payments/create-order` | `orderId`, `paymentId` (mock) |
| 14 | 14. Verify Payment | `POST /api/payments/verify` | `success: true`, appointment → `PAID`, `CONFIRMED` |
| 15 | 15. Appointment Pass | `GET /api/appointments/{id}/pass` | `qrData`, `tokenNumber`, `clinic`, `activePass: true`, `degree` |

**Android screens:** Payment Successful → Appointment Pass

---

### Appointments Tab

| Step | Postman | API | Expected |
|------|---------|-----|----------|
| 16 | 16. All | `GET /api/appointments/user/{userId}` | All appointments |
| 17 | 17. Upcoming | `.../upcoming` | Future non-cancelled |
| 18 | 18. Completed | `.../completed` | `status: COMPLETED` only |
| 19 | 19. Cancelled | `.../cancelled` | Cancelled only |

**Android screens:** Appointment tab with All / Completed / Upcoming / Cancelled filters

---

### Profile & Family

| Step | Postman | API | Expected |
|------|---------|-----|----------|
| 20 | 20. Get Profile | `GET /api/auth/profile/{userId}` | Name, phone, email, DOB, gender, bloodGroup |
| 21 | 21. List Family | `GET /api/family/{userId}` | Mother, Father (seed) |
| 22 | 22. Add Family Member | `POST /api/family/{userId}` | New member with id |

**Android screens:** Profile → Family Members → Add Member

---

## Android integration checklist

| Figma screen | API endpoint | Postman step |
|--------------|--------------|--------------|
| Send OTP | `POST /api/auth/send-otp` | 1 |
| Verify OTP | `POST /api/auth/verify-otp` | 2 |
| Create Profile | `POST /api/auth/complete-profile` | 3 |
| Home | `GET /api/home/{userId}` | 4 |
| Services | `GET /api/services` | 5 |
| Search | `GET /api/search?q=` | 6 |
| Specializations | `GET /api/specializations` | 7 |
| Doctor list ₹20 | `GET /api/doctors?maxFee=20&clinicType=DR20_CLINIC` | 8 |
| Doctor profile | `GET /api/doctors/{id}/detail` | 9 |
| Slots | `GET /api/doctors/{id}/slots?date=` | 10 |
| Book | `POST /api/appointments` | 11 |
| Bill summary | `GET /api/payments/summary/{id}` | 12 |
| Pay (mock) | `create-order` + `verify` | 13–14 |
| Appointment pass | `GET /api/appointments/{id}/pass` | 15 |
| Appointment tabs | `user/{id}`, `/upcoming`, `/completed`, `/cancelled` | 16–19 |
| Profile | `GET /api/auth/profile/{userId}` | 20 |
| Family | `GET/POST /api/family/{userId}` | 21–22 |

---

## Common issues

| Problem | Fix |
|---------|-----|
| 403 Forbidden | Remove duplicate Authorization headers; use Auth tab = No Auth |
| OTP invalid | Check Render logs; wait 60s before resend |
| Slot not available | Run step 10 first; pick a slot from response; update `appointmentTime` variable |
| Empty consultAt20Doctors | DB seeded with old data — redeploy with fresh Mongo or use filters on step 8 |
| `isProfileComplete: true` skips profile | Expected for seed phone `9876543210` |

---

## Deploy to Android team

After all Postman steps pass:

1. Share base URL: `https://dr20-backend.onrender.com`
2. Share this file + `PATIENT_FLOW.md`
3. Share Postman collection for reference
4. OTP: staging logs only (no SMS yet)

---

## What to test next (not in this screenshot set)

- Staff app (on hold)
- Nurse/home-care booking flow
- Real Razorpay payment
- Location picker API (currently static `Tambaram` in home/services)
