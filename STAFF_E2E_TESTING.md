# Dr.20 Staff E2E — Testing Guide

**Base URL:** `https://dr20-backend.onrender.com`  
**Postman:** `postman/Dr20-Staff-E2E.postman_collection.json`  
**Screen map:** `STAFF_FLOW.md`

---

## Before you start

1. **Re-import** the collection after updates (uses **folders** like Patient E2E).
2. **Auth:** Postman Auth tab = **No Auth**. Protected requests use `Authorization: Bearer {{staffToken}}`.
3. **OTP:** Copy from **Render Logs** after step `1. Send OTP`.
4. **Seed staff phone:** `9123456781` (Dr. Priya Menon)
5. **Verify flow:** token `07`, QR `DR20-SEED001` (after patient books + pays, or fresh seed)

---

## Recommended run order

| Step | Folder | Requests |
|------|--------|----------|
| 1 | `01 — Auth & Registration` | 1 → 2 (OTP login) |
| 2 | `03 — Dashboard & Bookings` | 3 Dashboard, 4 Upcoming, 4e Search |
| 3 | `04 — Appointments` | 4h Detail → 5 Verify → 7 Start → 8 Complete |
| 4 | `06 — Profile` | 10b Summary, 10c/10d READ, 13/10e UPDATE |
| 5 | `07 — Earnings` | 18 Summary, 19 Transactions |
| 6 | `05 — Schedule` | 11b Day view, 14/15 Working hours |
| 7 | `02 — Verification` | 20 Documents, 20b Upload, 20c Bank |

**Home visit path:** After step 4h Detail, run `4i Navigate` → `8b Arrive` → verify → complete.

**Full E2E with patient:** Run Patient collection book+pay first, then Staff verify token `07`.

---

## Staff API — CRUD matrix

| Resource | Create | Read | Update | Delete |
|----------|--------|------|--------|--------|
| **Auth/Register** | 0a Self Register, 0b Admin Staff | — | — | — |
| **OTP** | 1 Send OTP, 1b New, 1c Resend | — | 2 Verify | — |
| **Profile Personal** | — | 10, 10b, 10c | 13 Update, 10f Photo | — |
| **Profile Professional** | — | 10d | 10e Update | — |
| **Verification** | 21 Submit, 20b Upload Doc, 20c Bank | 17 Status, 20 Documents | — | — |
| **Dashboard/Bookings** | — | 3 Dashboard, 4/4b/4d Lists, 4c Counts | — | — |
| **Search** | — | 4e Token, 4f Name | — | — |
| **Appointments** | — | 4g ID, 4h Detail, 4i Navigate | 8b Arrive, 5/6 Verify, 7 Start, 8 Complete | — |
| **Schedule** | 12 Add Slots, 16/16b Block | 11 Range, 11b Day, 14 Hours | 15 Working Hours | — |
| **Earnings** | — | 18 Summary, 19 Transactions | — | — |

---

## Collection variables (auto-set)

| Variable | Set by |
|----------|--------|
| `staffToken`, `userId`, `linkedProfileId` | Step 2 Verify OTP |
| `scheduleDate`, `scheduleFrom`, `scheduleTo` | Step 2 Verify OTP |
| `appointmentId` | Dashboard, Upcoming, Search, Verify |
| `homeVisitAppointmentId` | Step 4h Detail (Home Nursing) |

---

## Pair with Patient E2E

1. Patient: book + pay → get token/QR from pass
2. Staff: `5. Verify Token` or `6. Verify QR`
3. Staff: `7. Start` → `8. Complete`
4. Patient: check completed appointments tab

See `PATIENT_E2E_TESTING.md` for patient side.
