# Dr.20 Backend — Testing Guide

## Local vs Live Staging

| | **Local** | **Live staging** |
|---|-----------|------------------|
| Base URL | `http://localhost:8081` | `{{baseUrl}}` e.g. `https://dr20-api.onrender.com` |
| Profile | `dev` | `staging` |
| MongoDB | `mongodb://localhost:27017/dr20db` | MongoDB Atlas (see `DEPLOY.md`) |
| OTP source | Your PC terminal | **Render/Railway deployment logs** |
| Postman | Import `postman/Dr20-Staff-Phase1.postman_collection.json` | Set collection variable `baseUrl` to your live URL |

**Deploy steps:** see **`DEPLOY.md`**.

---

## Live Staging — Staff Phase 1 (quick)

1. Deploy backend with `SPRING_PROFILES_ACTIVE=staging` (see `DEPLOY.md`).
2. In Postman: Import `postman/Dr20-Staff-Phase1.postman_collection.json`.
3. Edit collection variable **`baseUrl`** → your live URL (no trailing slash).
4. Run **1. Staff Send OTP**.
5. Open **host logs** → find `DEV OTP for 9123456781 : XXXXXX`.
6. Paste OTP in **2. Staff Verify OTP** → `staffToken` auto-saves.
7. Run requests **3 → 9** in order.

```powershell
# Example curl (replace {{baseUrl}})
curl -X POST {{baseUrl}}/api/staff/auth/send-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9123456781\"}"

curl {{baseUrl}}/api/staff/dashboard `
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Seeded staff phone:** `9123456781` · **Sample token:** `7842` · **Sample QR:** `DR20-SEED001`

---

# Local Development

Base URL: `http://localhost:8081`  
Profile: **dev** (OTP printed to console, mock payments)  
MongoDB: `mongodb://localhost:27017/dr20db`

---

## Before You Start

### 1. Start MongoDB
```powershell
net start MongoDB
```

### 2. Run the app
```powershell
cd "c:\Users\Dharani\DR Practice\doctor-patient-api\doctor-patient-api"
mvn spring-boot:run
```

### 3. Run unit tests (optional)
```powershell
mvn test
```

### 4. Dev OTP
After `send-otp`, check the **terminal/console** for:
```
DEV OTP for 9123456781 : 482917
```
Use that 6-digit code in `verify-otp`. It is **not** returned in the API response.

### 5. JWT token
After `verify-otp`, copy the `token` from the response.  
Add to all protected requests:
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## Seeded Test Data (first startup)

| Role | Phone | Notes |
|------|-------|-------|
| Staff doctor (Dr. Priya) | `9123456781` | Use for **Phase 1** staff testing |
| Staff doctor (Dr. Rajesh) | `9123456782` | Cardiology |
| Staff doctor (Dr. Amit) | `9123456783` | Neurology |
| Admin | `9000000001` | Admin role |
| Patient (Pravin M) | `9876543210` | Sample booking already seeded |

| Sample appointment | Value |
|--------------------|-------|
| Token number | `7842` |
| QR code | `DR20-SEED001` |
| Patient name | Pravin M |
| Status | CONFIRMED (ready to verify) |

> Seed runs once. If DB already has data, sample appointment may not re-create.  
> To reset: drop MongoDB database `dr20db` and restart the app.

---

# Phase 1 — Staff Testing (do this TODAY)

Test order: login → dashboard → verify → start → complete.

---

### Step 1 — Staff send OTP

**POST** `/api/staff/auth/send-otp`

```powershell
curl -X POST http://localhost:8081/api/staff/auth/send-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9123456781\"}"
```

Expected: `{ "success": true, "message": "OTP sent" }`  
Action: Read OTP from console.

---

### Step 2 — Staff verify OTP → get JWT

**POST** `/api/staff/auth/verify-otp`

```powershell
curl -X POST http://localhost:8081/api/staff/auth/verify-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9123456781\",\"otp\":\"YOUR_OTP_FROM_CONSOLE\"}"
```

Expected:
```json
{
  "success": true,
  "token": "eyJ...",
  "role": "DOCTOR",
  "linkedProfileId": "..."
}
```

Save `token` as `$STAFF_TOKEN` for next steps.

---

### Step 3 — Staff dashboard

**GET** `/api/staff/dashboard`

```powershell
curl http://localhost:8081/api/staff/dashboard `
  -H "Authorization: Bearer $STAFF_TOKEN"
```

Expected: `todayOverview.waiting`, `todayOverview.completed`, `upcomingAppointments`.

---

### Step 4 — Upcoming appointments

**GET** `/api/staff/appointments/upcoming`

```powershell
curl http://localhost:8081/api/staff/appointments/upcoming `
  -H "Authorization: Bearer $STAFF_TOKEN"
```

---

### Step 5 — Verify patient by token

**POST** `/api/staff/appointments/verify-token`

```powershell
curl -X POST http://localhost:8081/api/staff/appointments/verify-token `
  -H "Authorization: Bearer $STAFF_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"tokenNumber\":\"7842\"}"
```

Expected: appointment `status` → `VERIFIED`

---

### Step 6 — Verify patient by QR (alternative)

**POST** `/api/staff/appointments/verify-qr`

```powershell
curl -X POST http://localhost:8081/api/staff/appointments/verify-qr `
  -H "Authorization: Bearer $STAFF_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"qrData\":\"DR20-SEED001\"}"
```

---

### Step 7 — Start consultation

**POST** `/api/staff/appointments/{id}/start`

Replace `{id}` with appointment ID from step 4 or 5.

```powershell
curl -X POST http://localhost:8081/api/staff/appointments/APPOINTMENT_ID/start `
  -H "Authorization: Bearer $STAFF_TOKEN"
```

Expected: `status` → `IN_PROGRESS`

---

### Step 8 — Complete appointment

**POST** `/api/staff/appointments/{id}/complete`

```powershell
curl -X POST http://localhost:8081/api/staff/appointments/APPOINTMENT_ID/complete `
  -H "Authorization: Bearer $STAFF_TOKEN"
```

Expected: `status` → `COMPLETED`

---

### Step 9 — Dashboard counts updated

**GET** `/api/staff/dashboard` again — `completed` count should increase.

---

### Step 10 — Schedule (optional)

**GET** `/api/staff/schedule?from=2026-06-28&to=2026-07-04`

```powershell
curl "http://localhost:8081/api/staff/schedule?from=2026-06-28&to=2026-07-11" `
  -H "Authorization: Bearer $STAFF_TOKEN"
```

**POST** `/api/staff/schedule` — add slots for a date:

```powershell
curl -X POST http://localhost:8081/api/staff/schedule `
  -H "Authorization: Bearer $STAFF_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"date\":\"2026-07-15\",\"times\":[\"05:00 PM\",\"06:00 PM\"]}"
```

---

### Step 11 — Staff profile

```powershell
curl http://localhost:8081/api/staff/profile `
  -H "Authorization: Bearer $STAFF_TOKEN"
```

---

### Phase 1 checklist

- [ ] Send OTP
- [ ] Verify OTP → JWT
- [ ] Dashboard loads
- [ ] Verify token `7842`
- [ ] Start consultation
- [ ] Complete appointment
- [ ] Dashboard completed count updates

---

# Staff Registration (optional — before Phase 2)

Maps to Figma **Create Account** (self) or admin adding staff. **No JWT required.**

**Live base URL:** `https://dr20-backend.onrender.com`  
**Postman:** requests **0a** and **0b** in `postman/Dr20-Staff-Phase1.postman_collection.json`

After register, set collection variable `staffPhone` to the new phone, then run **1. Send OTP → 2. Verify OTP → 3. Dashboard**.

### Self registration (staff app — Create Account)

**POST** `/api/staff/auth/register`

```powershell
curl -X POST https://dr20-backend.onrender.com/api/staff/auth/register `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9222222222\",\"profession\":\"DOCTOR\",\"firstName\":\"Anita\",\"lastName\":\"Sharma\",\"email\":\"anita@dr20.com\"}"
```

| Field | Required | Notes |
|-------|----------|-------|
| `phone` | Yes | 10 digits, must be **new** (not already in DB) |
| `profession` | Yes | `DOCTOR`, `NURSE`, `PHYSIOTHERAPIST`, `LAB_TECH`, `ELDER_CARE` |
| `firstName`, `lastName`, `email` | Optional | Creates user + doctor profile + 14 days slots |

Expected: `{ "success": true, "message": "Staff registered. Send OTP to login.", "userId", "doctorId" }`

Then OTP login with phone `9222222222` (steps 1–2 in Postman).

### Admin create staff (admin panel)

**POST** `/api/admin/staff`

```powershell
curl -X POST https://dr20-backend.onrender.com/api/admin/staff `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9111111111\",\"firstName\":\"Test\",\"lastName\":\"Doctor\",\"profession\":\"DOCTOR\",\"specialization\":\"Dermatology\",\"consultationFee\":\"700\",\"clinicName\":\"Test Clinic\",\"clinicAddress\":\"Test Address\"}"
```

Creates staff with clinic details. Then OTP login with phone `9111111111`.

### Self vs admin

| | Self register | Admin create |
|---|---------------|--------------|
| API | `/api/staff/auth/register` | `/api/admin/staff` |
| UI | Staff **Create Account** | Admin adds doctor |
| Duplicate phone | Rejected | Updates existing user if phone exists |

### Registration checklist

- [ ] Self register OR admin create → 200 OK
- [ ] Set `staffPhone` to new phone in Postman variables
- [ ] Send OTP → Verify OTP → Dashboard (may be empty until patients book)

---

# Phase 2 — Patient Testing (do LATER)

---

### Step 1 — Patient send OTP

**POST** `/api/auth/send-otp`

```powershell
curl -X POST http://localhost:8081/api/auth/send-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9998887776\"}"
```

---

### Step 2 — Patient verify OTP

**POST** `/api/auth/verify-otp`

```powershell
curl -X POST http://localhost:8081/api/auth/verify-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9998887776\",\"otp\":\"YOUR_OTP\"}"
```

Save token as `$PATIENT_TOKEN`.

---

### Step 3 — Complete profile

**POST** `/api/auth/complete-profile`

```powershell
curl -X POST http://localhost:8081/api/auth/complete-profile `
  -H "Authorization: Bearer $PATIENT_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@test.com\",\"gender\":\"Male\",\"bloodGroup\":\"O+\"}"
```

---

### Step 4 — Home screen

**GET** `/api/home/{userId}`

```powershell
curl http://localhost:8081/api/home/USER_ID `
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

---

### Step 5 — List doctors & get slots

```powershell
curl http://localhost:8081/api/doctors `
  -H "Authorization: Bearer $PATIENT_TOKEN"

curl "http://localhost:8081/api/doctors/DOCTOR_ID/slots?date=2026-07-15" `
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

Use today's date or any future date (`YYYY-MM-DD`).

---

### Step 6 — Book appointment

**POST** `/api/appointments`

```powershell
curl -X POST http://localhost:8081/api/appointments `
  -H "Authorization: Bearer $PATIENT_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"doctorId\":\"DOCTOR_ID\",\"appointmentDate\":\"2026-07-15\",\"appointmentTime\":\"10:00 AM\",\"consultationType\":\"In-Person\",\"patientName\":\"John Doe\",\"symptoms\":\"Fever\"}"
```

---

### Step 7 — Payment summary

**GET** `/api/payments/summary/{appointmentId}`

```powershell
curl http://localhost:8081/api/payments/summary/APPOINTMENT_ID `
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

---

### Step 8 — Create payment order (mock)

**POST** `/api/payments/create-order`

```powershell
curl -X POST http://localhost:8081/api/payments/create-order `
  -H "Authorization: Bearer $PATIENT_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"appointmentId\":\"APPOINTMENT_ID\",\"userId\":\"USER_ID\"}"
```

---

### Step 9 — Verify payment (mock — always succeeds in dev)

**POST** `/api/payments/verify`

```powershell
curl -X POST http://localhost:8081/api/payments/verify `
  -H "Authorization: Bearer $PATIENT_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"orderId\":\"ORDER_ID\",\"paymentId\":\"PAYMENT_ID\",\"signature\":\"mock\"}"
```

Expected: appointment status → `CONFIRMED`

---

### Step 10 — Appointment pass (QR + token)

**GET** `/api/appointments/{id}/pass`

```powershell
curl http://localhost:8081/api/appointments/APPOINTMENT_ID/pass `
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

Returns `qrData`, `tokenNumber`, clinic address.

---

### Step 11 — Upcoming / past / cancel

```powershell
curl http://localhost:8081/api/appointments/user/USER_ID/upcoming `
  -H "Authorization: Bearer $PATIENT_TOKEN"

curl http://localhost:8081/api/appointments/user/USER_ID/past `
  -H "Authorization: Bearer $PATIENT_TOKEN"

curl -X PUT http://localhost:8081/api/appointments/APPOINTMENT_ID/cancel `
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

---

### Step 12 — Family member

```powershell
curl -X POST http://localhost:8081/api/family/USER_ID `
  -H "Authorization: Bearer $PATIENT_TOKEN" `
  -H "Content-Type: application/json" `
  -d "{\"name\":\"Jane Doe\",\"relation\":\"Spouse\",\"age\":30,\"gender\":\"Female\"}"
```

---

### Phase 2 checklist

- [ ] Patient OTP login
- [ ] Profile created
- [ ] Home data loads
- [ ] Slots for future date
- [ ] Book appointment
- [ ] Mock payment
- [ ] Get appointment pass
- [ ] Upcoming list shows booking

---

# Phase 3 — End-to-End (Patient + Staff together)

```
1. Patient books + pays        → gets qrData + tokenNumber
2. Staff logs in               → dashboard shows appointment
3. Staff verify-token or QR    → VERIFIED
4. Staff start                 → IN_PROGRESS
5. Staff complete              → COMPLETED
6. Patient past appointments   → shows COMPLETED
```

Use the `qrData` and `tokenNumber` from **Step 10 (Phase 2)** in staff verify APIs.

---

# Troubleshooting

| Issue | Fix |
|-------|-----|
| `401 Unauthorized` | Add `Authorization: Bearer TOKEN` header |
| `403 Forbidden` | Wrong role — use staff token for `/api/staff/*` |
| OTP invalid | Check console for latest OTP; expires in 5 min |
| Too many OTP requests | Wait 15 min or clear `otp_logs` in MongoDB |
| Phone already registered | Use a new phone for self-register |
| No seeded appointment | Drop DB `dr20db` and restart app |
| MongoDB connection failed | Run `net start MongoDB` (local) or check Atlas URI + IP allowlist (staging) |
| Port in use | Cloud: host sets `PORT`; local: change `server.port` in `application.properties` |
| Staging OTP missing | Check **Render/Railway logs**, not local terminal |
| Cold start timeout | Wait 30–60s on Render free tier, retry |

---

# Reset database (fresh seed)

```powershell
mongosh dr20db --eval "db.dropDatabase()"
mvn spring-boot:run
```

---

# API summary

| Group | Base path |
|-------|-----------|
| Patient auth | `/api/auth` |
| Staff auth | `/api/staff/auth` |
| Staff app | `/api/staff` |
| Doctors | `/api/doctors` |
| Appointments | `/api/appointments` |
| Payments | `/api/payments` |
| Home / search | `/api/home`, `/api/search` |
| Family | `/api/family` |
| Admin | `/api/admin` |
