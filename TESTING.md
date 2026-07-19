# Dr.20 Backend — Testing Guide

**Live URL:** `https://dr20-backend.onrender.com`  
**Deploy:** see `DEPLOY.md`  
**Mongo reset:** see `MONGO_RESET.md`

---

## Postman collections

| App | Collection | Guide |
|-----|------------|-------|
| **Patient** | `postman/Dr20-Patient-E2E.postman_collection.json` | `PATIENT_E2E_TESTING.md` |
| **Staff** | `postman/Dr20-Staff-E2E.postman_collection.json` | `STAFF_E2E_TESTING.md` |

**Screen → API map (staff):** `STAFF_FLOW.md`

**Screen → API map (patient):** `PATIENT_FLOW.md`

---

## Quick start (live)

1. Import the collection for the app you are testing.
2. Set collection variable **`baseUrl`** → `https://dr20-backend.onrender.com` (no trailing slash).
3. Run **Send OTP** → copy OTP from **Render deployment logs** (not your PC terminal).
4. Run **Verify OTP** → `patientToken` or `staffToken` auto-saves.
5. Run remaining requests in order.

**Cold start:** Render free tier may take 30–60s on first request.

---

## Seeded test data (fresh MongoDB only)

Seed runs once when collections are empty. See `MONGO_RESET.md` if you see old data.

| Role | Phone | Notes |
|------|-------|-------|
| Patient | `9876543210` | Name: pravin av, profile complete |
| Staff (Dr. Priya) | `9123456781` | For QR/token verify |
| Upcoming token | `07` | Seed appointment |
| QR code | `DR20-SEED001` | Seed appointment |

---

## Local development

| | Value |
|---|--------|
| Base URL | `http://localhost:8081` |
| Profile | `dev` |
| MongoDB | `mongodb://localhost:27017/dr20db` |
| OTP | Local terminal after send-otp |
| Run app | `mvn spring-boot:run` |
| Unit tests | `mvn test` |

Reset local DB:

```powershell
mongosh dr20db --eval "db.dropDatabase()"
mvn spring-boot:run
```

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `401 Unauthorized` | Add `Authorization: Bearer TOKEN` header |
| `403 Forbidden` | Wrong role — patient token for `/api/*`, staff token for `/api/staff/*` |
| OTP invalid | Check latest OTP in logs; expires in 5 min |
| Old doctors (₹600) or token `7842` | Mongo reset — see `MONGO_RESET.md` |
| Missing `greeting` or `/api/services` 404 | Deploy latest code on Render |
| MongoDB connection failed | Local: `net start MongoDB`; Atlas: check URI + IP allowlist |
| Slow first request | Render cold start — wait and retry |

---

## API groups

| Group | Base path |
|-------|-----------|
| Patient auth | `/api/auth` |
| Patient home / services | `/api/home`, `/api/services`, `/api/search` |
| Doctors | `/api/doctors` |
| Appointments | `/api/appointments` |
| Payments | `/api/payments` |
| Family | `/api/family` |
| Staff auth | `/api/staff/auth` |
| Staff app | `/api/staff` |
| Admin | `/api/admin` |
