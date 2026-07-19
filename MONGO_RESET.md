# Mongo Atlas Reset Guide — Fresh Dr.20 Seed Data

**Database:** `dr20db` (on your Atlas cluster)  
**Purpose:** Load Figma-aligned demo data after clearing old records.

**⚠️ You must confirm before deleting anything.** This doc only lists what to clear and what you get after redeploy.

---

## When you need this reset

Clear collections if you see **old** data:

| Symptom | Old data |
|---------|----------|
| Doctors fee ₹600+ | Old seed |
| No `clinicType: DR20_CLINIC` | Old seed |
| Patient not `pravin av` | Old / missing seed |
| Token `7842` instead of `07` | Old seed |
| Postman step 4 missing `greeting` | Old **deploy** (fix: git push + Render deploy) |
| Empty `family_members` | Seed skipped |

---

## Step 1 — Deploy latest code first

1. Push latest code to GitHub (`origin/master`).
2. Render → **Manual Deploy** → **Clear build cache & deploy**.
3. Wait until status = **Live**.
4. Check Render **Logs** for startup without errors.

New code adds: `greeting`, `consultAt20Doctors`, `/api/services`, `/completed` appointments, ₹20 doctor filters.

---

## Step 2 — Collections to clear (confirm with you before delete)

Open Atlas → **Browse Collections** → `dr20db`.

### Required for fresh patient + ₹20 doctor demo

| Collection | Why clear |
|------------|-----------|
| `doctors` | Old fees/clinics; seed runs only when empty |
| `appointments` | Old tokens/appointments; seed patient created here |
| `availability` | Slots tied to old doctor IDs |
| `family_members` | Mother/Father seed skipped if not empty |

### Recommended for matching Figma home/services UI

| Collection | Why clear |
|------------|-----------|
| `banners` | New banner text ("Healthcare at Your Doorstep") |
| `service_categories` | New "Dr at ₹20", "Private Clinics" labels |
| `specializations` | Optional — only if you want clean specialty list |

### Patient/staff users — choose one approach

| Approach | Clear | Result |
|----------|-------|--------|
| **A — Full demo reset** | `users` (entire collection) | Fresh patient `9876543210`, staff `9123456781`–`83`, admin |
| **B — Keep your login** | Do **not** clear `users` | Seed **skips** patient `9876543210` if appointments empty but user exists — run Postman step 3 for profile |

**Recommendation for Android demo:** **Approach A** — clear `users` too.

### Optional / staff extras (clear if you want staff verification seed)

| Collection | Notes |
|------------|-------|
| `staff_verifications` | Re-seeds for Dr. Priya staff |
| `staff_documents` | Re-seeds verification docs |
| `notifications` | Old test notifications |
| `medical_records` | Old test records |

### Do NOT clear (usually)

| Collection | Why keep |
|------------|----------|
| `otp_logs` | Only clear if OTP rate-limited |
| `admin` config | N/A |

---

## Step 3 — How to delete in Atlas (you do this manually)

For each collection listed above that you **confirmed**:

1. Atlas → `dr20db` → collection name  
2. **Delete** → **Delete all documents** (or drop collection)  
3. Repeat for each collection  

**Or** drop entire `dr20db` and let the app recreate collections on first write (simplest full reset).

---

## Step 4 — Trigger seed after clear

1. Render → **Restart** service (or wait for redeploy after push).  
2. On startup, `DataSeedService` runs `@PostConstruct seed()`.  
3. Check Render **Logs** for lines like:

```
Seeded staff doctor: 9123456781 / doctorId: ...
Seeded sample appointment — token: 07, qr: DR20-SEED001
Patient test phone: 9876543210
```

---

## Expected data after fresh seed

| Item | Value |
|------|--------|
| Patient phone | `9876543210` |
| Patient name | pravin av |
| Patient email | pravin@dr20.com |
| Blood group | O+ |
| Dr. Priya Menon | ₹20, Arokya Clinic, `DR20_CLINIC` |
| Dr. Arun Kumar | ₹20, Nalam Clinic, `DR20_CLINIC` |
| Dr. Harini Balaji | ₹300, `PRIVATE_CLINIC` |
| Upcoming appointment | token `07`, QR `DR20-SEED001`, tomorrow 09:30 AM |
| Completed appointment | Dr. Harini, status COMPLETED |
| Family | Mother, Father |
| Staff login | `9123456781` (Dr. Priya) |

---

## Step 5 — Verify in Postman

Import `postman/Dr20-Patient-E2E.postman_collection.json`.

| Step | Check |
|------|--------|
| 1–2 | OTP login `9876543210` |
| 4 | `greeting`, `consultAt20Doctors`, `nextAppointment` |
| 8 | Doctors with `consultationFee: 20` |
| 12 | `totalPayable: 20`, `platformFeeWaived: true` |
| 21 | Mother, Father in family list |

---

## Confirmation checklist (reply yes/no before delete)

Please confirm which you want to clear:

- [ ] `doctors`
- [ ] `appointments`
- [ ] `availability`
- [ ] `family_members`
- [ ] `banners`
- [ ] `service_categories`
- [ ] `specializations`
- [ ] `users` (full user reset)
- [ ] `staff_verifications` + `staff_documents`
- [ ] `notifications` + `medical_records`
- [ ] Or **drop entire `dr20db`**

**Nothing is deleted until you confirm.**
