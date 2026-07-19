# Dr.20 Patient E2E — Testing Guide



**Base URL:** `https://dr20-backend.onrender.com`  

**Postman:** Import `postman/Dr20-Patient-E2E.postman_collection.json`



---



## Before you start



1. **Cold start:** Render free tier may take 30–60s on first request.

2. **Auth:** Postman Auth tab = **No Auth**. Protected requests use one header: `Authorization: Bearer {{patientToken}}`.

3. **OTP:** After Send OTP, copy OTP from **Render Logs** (staging prints OTP in console).

4. **Re-import collection** after updates — collection uses **folders** with full CRUD coverage.

5. **Two test paths:**

   - **Path A — Seed patient:** phone `9876543210` (profile already complete on fresh DB)

   - **Path B — New signup:** use step `1b` with `9444444444`, then steps 2 → 3



---



## Patient API — CRUD matrix



| Resource | Create | Read | Update | Delete / Cancel |

|----------|--------|------|--------|-----------------|

| **Profile** | 3. Complete Profile | 20. Get Profile | 20b. Update Profile | — |

| **Appointments** | 11. Book | 15b, 16–19 | 11b. Reschedule | 19a. Cancel |

| **Payments** | 13. Create Order | 12. Summary, 24. History | 14. Verify (status) | — |

| **Family** | 22. Add | 21. List | 22b. Update | 22c. Delete |

| **Notifications** | (auto on book/pay) | 23. List, 23b. Unread | 23c/23d. Mark read | — |

| **Reviews** | 25. Submit | 25b, 25c. Doctor reviews | — | — |

| **Medical records** | 26c. Add | 26. List, 26b. By ID | — | — |

| **Discovery** | — | 4–10. Home, doctors, slots | — | — |



---



## Recommended full E2E run order



Run folders **in order**. Steps marked **(CRUD)** are new write/update/delete operations.



### 01 — Auth & Profile

| Step | API | Expected |

|------|-----|----------|

| 1 → 2 | OTP login | `token`, `userId` saved |

| 3 | Complete profile | Only if `isProfileComplete: false` |

| 20 | Get profile | User details |

| 20b | Update profile **(CRUD)** | Email/address updated |



### 02 — Discovery (READ)

| Step | API | Expected |

|------|-----|----------|

| 4–9 | Home, services, search, doctors | Data loads; step 8 saves `doctorId`, `appointmentDate`, `rescheduleDate` |

| 10 | Doctor slots | Auto-saves first slot → `appointmentTime` |

| 10b | Slots for reschedule date | Auto-saves `rescheduleTime` |



### 03 — Book & Pay

| Step | API | Expected |

|------|-----|----------|

| 11 | Book **(CREATE)** | `appointmentId`, `status: UPCOMING` |

| 11b | Reschedule **(UPDATE)** | Date/time changed to `rescheduleDate` / `rescheduleTime` |

| 11c | Book second **(CREATE)** | `cancelAppointmentId` — used only for cancel test |

| 12–14 | Summary → pay | `CONFIRMED`, `PAID` |

| 15 | Appointment pass | `qrData`, `tokenNumber` |



### 04 — Appointments (CRUD)

| Step | API | Expected |

|------|-----|----------|

| 15b | Get by ID **(READ)** | Single appointment |

| 16 | All | Full list |

| 17 | Upcoming | Future appointments |

| 17b | Past **(READ)** | Past dates |

| 18 | Completed | `COMPLETED` only — empty until staff completes (see below) |

| 19a | Cancel **(CANCEL)** | Cancels `cancelAppointmentId` (second booking) |

| 19 | Cancelled list | Shows cancelled appointment |



### 05 — Payments (READ)

| Step | API | Expected |

|------|-----|----------|

| 24 | Payment history | List with `SUCCESS` payments |



### 06 — Family (CRUD)

| Step | API | Expected |

|------|-----|----------|

| 21 | List | Mother, Father (seed) + added members |

| 22 | Add **(CREATE)** | `familyMemberId` saved |

| 22b | Update **(UPDATE)** | Name/age updated |

| 22c | Delete **(DELETE)** | Member removed |



### 07 — Notifications

| Step | API | Expected |

|------|-----|----------|

| 23 | List | Notifications from book/pay; saves `notificationId` |

| 23b | Unread count | `{ count: N }` |

| 23c | Mark read | Single notification `read: true` |

| 23d | Mark all read | All marked read |



### 08 — Reviews

| Step | API | Expected |

|------|-----|----------|

| 25 | Submit **(CREATE)** | Needs `completedAppointmentId` — staff must complete first |

| 25b–25c | Doctor reviews | List + summary |



### 09 — Medical Records

| Step | API | Expected |

|------|-----|----------|

| 26 | List | Seed record + staff-created records |

| 26b | Get by ID | Single record |

| 26c | Add **(CREATE)** | New record saved |



---



## Staff steps required for Completed + Review



Payment alone does **not** complete an appointment. Use **Dr.20 Staff App** collection:



1. Login as booked doctor (`9123456781` for Dr. Priya)

2. Verify with **your** `tokenNumber` or `qrData` from step 15

3. Start → Complete using **your** `appointmentId` from step 11

4. Patient step **18** → saves `completedAppointmentId`

5. Patient step **25** → submit review



---



## Android integration checklist



| Figma screen | API endpoint | Postman step |

|--------------|--------------|--------------|

| Send OTP | `POST /api/auth/send-otp` | 1 |

| Verify OTP | `POST /api/auth/verify-otp` | 2 |

| Create Profile | `POST /api/auth/complete-profile` | 3 |

| Edit Profile | `PUT /api/auth/profile/{userId}` | 20b |

| Home | `GET /api/home/{userId}` | 4 |

| Services | `GET /api/services` | 5 |

| Search | `GET /api/search?q=` | 6 |

| Specializations | `GET /api/specializations` | 7 |

| Doctor list ₹20 | `GET /api/doctors?maxFee=20&clinicType=DR20_CLINIC` | 8 |

| Doctor profile | `GET /api/doctors/{id}/detail` | 9 |

| Slots | `GET /api/doctors/{id}/slots?date=` | 10 |

| Book | `POST /api/appointments` | 11 |

| Reschedule | `PUT /api/appointments/{id}/reschedule` | 11b |

| Cancel | `PUT /api/appointments/{id}/cancel` | 19a |

| Bill summary | `GET /api/payments/summary/{id}` | 12 |

| Pay (mock) | `create-order` + `verify` | 13–14 |

| Payment history | `GET /api/payments/user/{userId}` | 24 |

| Appointment pass | `GET /api/appointments/{id}/pass` | 15 |

| Appointment tabs | `user/{id}`, `/upcoming`, `/past`, `/completed`, `/cancelled` | 16–19 |

| Notifications | `GET /api/notifications/user/{userId}` | 23 |

| Submit review | `POST /api/reviews` | 25 |

| Medical records | `GET/POST /api/medical-records/user/{userId}` | 26, 26c |

| Profile | `GET /api/auth/profile/{userId}` | 20 |

| Family CRUD | `GET/POST/PUT/DELETE /api/family/...` | 21–22c |



---



## Common issues



| Problem | Fix |

|---------|-----|

| 403 Forbidden | Remove duplicate Authorization headers; use Auth tab = No Auth |

| OTP invalid | Check Render logs; wait 60s before resend |

| `ENOTFOUND dr20-backend.onrender.com` | Network/DNS issue — check internet, VPN, retry; open URL in browser |

| Slot not available | Run step 10 first; slots auto-save to `appointmentTime` |

| Empty completed (step 18) | Staff must verify → start → complete **your** `appointmentId` |

| Empty cancelled (step 19) | Run **11c** then **19a** (cancels second booking, not main one) |

| Reschedule fails | Run **10b** first for `rescheduleDate` slots |

| Review fails | Appointment must be `COMPLETED`; run staff complete + step 18 first |

| `Not your appointment` on cancel | Wrong `patientToken` or wrong `appointmentId` |

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



## What to test next (not in patient E2E)



- Staff app full flow (`Dr20-Staff-E2E.postman_collection.json`)

- Nurse/home-care booking flow

- Real Razorpay payment

- Location picker API (currently static `Tambaram` in home/services)

