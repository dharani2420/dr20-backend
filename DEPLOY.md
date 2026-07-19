# Dr.20 — Live Staging Deployment

Deploy the backend to a public URL so mobile apps and Postman can test without `localhost`.

**Recommended stack:** MongoDB Atlas + Render (or Railway)  
**Profile:** `staging` (console OTP in server logs, mock payments)

---

## Prerequisites

- GitHub repo with this project pushed
- [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) account (free tier)
- [Render](https://render.com) or [Railway](https://railway.app) account

---

## Step 1 — MongoDB Atlas

1. Create a **free M0 cluster**.
2. **Database Access** → Add user (username + strong password). Role: `Atlas admin` or read/write on `dr20db`.
3. **Network Access** → Add IP Address → **Allow Access from Anywhere** (`0.0.0.0/0`) for staging.
4. **Connect** → Drivers → copy the connection string:

```
mongodb+srv://USERNAME:PASSWORD@cluster0.xxxxx.mongodb.net/dr20db?retryWrites=true&w=majority
```

Replace `USERNAME`, `PASSWORD`, and ensure database name is `dr20db`.

5. Save this URI — you will paste it as `SPRING_DATA_MONGODB_URI` (never commit it to git).

---

## Step 2 — Generate JWT secret

Use a long random string (minimum 256 bits / 32+ characters):

```powershell
# PowerShell — example
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 48 | ForEach-Object {[char]$_})
```

Save as `JWT_SECRET` in the hosting dashboard only.

---

## Step 3 — Deploy on Render (Docker — recommended)

Render often defaults to Node.js and fails with `mvn: command not found`. Use **Docker** instead.

1. Push code to GitHub (includes `Dockerfile` at project root next to `pom.xml`).
2. Render → your service → **Settings**.
3. Configure:

| Field | Value |
|-------|--------|
| **Runtime** | **Docker** |
| **Root Directory** | Folder containing `Dockerfile` and `pom.xml` (blank if repo root **is** the Spring Boot project) |
| **Dockerfile Path** | `Dockerfile` (default) |
| **Build Command** | **Leave empty** |
| **Start Command** | **Leave empty** |

Build and start are defined in `Dockerfile` — do not use `mvn` in Render build command when Runtime is Docker.

4. **Environment** variables:

| Key | Value |
|-----|--------|
| `SPRING_PROFILES_ACTIVE` | `staging` |
| `SPRING_DATA_MONGODB_URI` | your Atlas URI |
| `JWT_SECRET` | your generated secret |

Render sets `PORT` automatically — the app uses `server.port=${PORT:8081}`.

5. **Manual Deploy** → **Clear build cache & deploy**.
6. Check **Logs** — you should see Maven/Java build steps, **not** `Using Node.js`.
7. Copy your service URL, e.g. `https://dr20-backend.onrender.com`.

**First request** on free tier may take 30–60s (cold start).

### Render Docker checklist

- [ ] `Dockerfile` pushed to GitHub (same folder as `pom.xml`)
- [ ] Runtime = **Docker**
- [ ] Root Directory points to that folder
- [ ] Build/Start commands empty
- [ ] 3 env vars set
- [ ] Logs show `Started Dr20Application`

---

## Step 3 (alternative) — Render Native Java

Only if Docker is not used and Render detects `pom.xml` at service root:

| Field | Value |
|-------|--------|
| Root Directory | folder with `pom.xml` |
| Build Command | `mvn clean package -DskipTests` |
| Start Command | `java -jar target/doctor-patient-0.0.1-SNAPSHOT.jar` |

If logs show `Using Node.js`, switch to **Docker** above.

---

## Step 3 (alternative) — Deploy on Railway

1. New Project → **Deploy from GitHub repo**.
2. Set root to `doctor-patient-api/doctor-patient-api` if needed.
3. Add the same environment variables as Render.
4. Railway detects Java/Maven or use:

   - Build: `mvn clean package -DskipTests`
   - Start: `java -jar target/doctor-patient-0.0.1-SNAPSHOT.jar`

5. Copy the public URL from Railway dashboard.

---

## Step 4 — Verify deployment

Replace `YOUR_URL` with your live base URL.

```powershell
curl -X POST https://YOUR_URL/api/staff/auth/send-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9123456781\"}"
```

Expected: `{ "success": true, "message": "OTP sent" }`

### Read OTP on staging

OTP is **not** in the API response. Open **Render/Railway logs** and search for:

```
DEV OTP for 9123456781 : 123456
```

Then verify OTP:

```powershell
curl -X POST https://YOUR_URL/api/staff/auth/verify-otp `
  -H "Content-Type: application/json" `
  -d "{\"phone\":\"9123456781\",\"otp\":\"123456\"}"
```

Use the returned `token` for staff APIs. See `TESTING.md` and `postman/Dr20-Staff-E2E.postman_collection.json`.

---

## Environment variables reference

| Variable | Required | Description |
|----------|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | Yes (staging) | `staging`, `dev`, or `prod` |
| `SPRING_DATA_MONGODB_URI` | Yes (cloud) | MongoDB Atlas connection string |
| `JWT_SECRET` | Yes (staging/prod) | JWT signing key; min 256 bits |
| `PORT` | Auto | Set by Render/Railway; do not hardcode |

### Profile behavior

| Profile | OTP | Payment | Use case |
|---------|-----|---------|----------|
| `dev` | Console (local terminal) | Mock | Local development |
| `staging` | Console (host logs) | Mock | Cloud testing before prod |
| `prod` | MSG91 SMS | Razorpay | Production |

For `prod`, also set: `MSG91_API_KEY`, `RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, etc. (see `application-prod.properties`).

---

## Seeded data (first startup)

On first run with an empty database, `DataSeedService` creates:

| Item | Value |
|------|-------|
| Staff doctor (Dr. Priya) | phone `9123456781` |
| Patient | phone `9876543210`, name `pravin av` |
| Sample token | `07` |
| Sample QR | `DR20-SEED001` |

To reset Atlas data: Atlas → Browse Collections → drop `dr20db`, or:

```javascript
// mongosh with Atlas connection string
use dr20db
db.dropDatabase()
```

Then restart the deployed service.

---

## Mobile / frontend configuration

Point the app API base URL to your live host:

```javascript
const API_BASE = 'https://dr20-backend.onrender.com';
```

Store JWT in secure storage after OTP login; attach `Authorization: Bearer <token>` on every request.

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| Build fails | Ensure Java 17; run `mvn clean package -DskipTests` locally first |
| App crashes on start | Check logs; verify `SPRING_DATA_MONGODB_URI` and Atlas IP allowlist |
| MongoDB connection timeout | Atlas → Network Access → allow `0.0.0.0/0` for staging |
| 403 on staff APIs | Add `Authorization: Bearer <token>` from verify-otp |
| OTP not found | Check **deployment logs**, not your PC terminal |
| Empty dashboard | Login as `9123456781`; confirm seed ran (empty DB on first deploy) |
| Slow first request | Render free tier cold start — wait and retry |
| JWT errors | Set `JWT_SECRET` on host; must be long enough for HMAC |

---

## Security checklist (before production)

- [ ] Use `prod` profile with MSG91 + Razorpay
- [ ] Strong unique `JWT_SECRET` per environment
- [ ] Restrict Atlas IP allowlist (not `0.0.0.0/0`) if possible
- [ ] HTTPS only (Render/Railway provide this)
- [ ] Never commit `.env`, Atlas URI, or API keys to git

---

## Local vs staging quick reference

| | Local | Staging |
|---|--------|---------|
| Base URL | `http://localhost:8081` | `https://YOUR_URL` |
| Profile | `dev` | `staging` |
| MongoDB | Local `27017` | Atlas |
| OTP | PC terminal | Render/Railway logs |
| Postman `baseUrl` | `http://localhost:8081` | `https://YOUR_URL` |

See **TESTING.md**, **PATIENT_E2E_TESTING.md**, and **PATIENT_FLOW.md**.
