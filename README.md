# 🏥 HealthFit — Android Health Tracker App

A comprehensive health tracking Android app similar to Google Fit, with dedicated **Diabetic Glucose Tracking** (pre/post meal) and **Blood Pressure Monitoring**.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🚶 **Step & Activity Tracker** | Log walks, runs, cycling, yoga, gym, swimming & more |
| 🩸 **Blood Glucose Tracker** | Pre-meal, post-meal, fasting, bedtime readings with diabetic targets |
| 💓 **Blood Pressure Tracker** | Systolic/diastolic/pulse with WHO classification |
| 📊 **Dashboard** | Real-time summary of today's health metrics |
| 📋 **History & Reports** | Lifetime summaries, averages, and breakdowns |
| 🗄️ **Local Storage** | All data stored locally using Room (SQLite) |

---

## 📱 App Screens

```
┌─────────────────────────────────────────────┐
│  Dashboard  │  Glucose  │  BP  │  Activity  │  History
└─────────────────────────────────────────────┘
```

### 🩸 Glucose Tracking
- **Measurement types**: Fasting, Pre-meal, Post-meal, Bedtime, Random
- **Diabetic targets displayed**: 80–130 pre-meal, <180 post-meal
- **Color-coded status**: Normal 🟢 | Pre-Diabetic 🟡 | High 🔴 | Low 🔵

### 💓 Blood Pressure
- **WHO Classification**: Normal → Elevated → High Stage 1 → High Stage 2 → Crisis
- **Records**: Systolic, Diastolic, Pulse rate
- Includes reference guide in-app

### 🏃 Activity Tracking
- 9 activity types (Walking, Running, Cycling, Swimming, Yoga, Gym, Hiking, Dancing, Other)
- Tracks: Duration, Calories, Distance, Steps
- Daily step goal with progress bar

---

## 🚀 How to Create on GitHub & Auto-Build APK

### Step 1 — Create GitHub Repository

1. Go to [github.com](https://github.com) → **New repository**
2. Name it `HealthFit`
3. Set to **Public** (required for free GitHub Actions minutes)
4. **Do NOT** initialize with README (you'll push this code)
5. Click **Create repository**

### Step 2 — Upload This Code

**Option A — GitHub Desktop (easiest):**
1. Download [GitHub Desktop](https://desktop.github.com/)
2. File → Clone repository → your new `HealthFit` repo
3. Copy all files from this project into the cloned folder
4. In GitHub Desktop: write commit message → **Commit to main** → **Push origin**

**Option B — Command Line (git):**
```bash
# In the HealthFit folder
git init
git add .
git commit -m "Initial commit: HealthFit app"
git remote add origin https://github.com/YOUR_USERNAME/HealthFit.git
git branch -M main
git push -u origin main
```

**Option C — Upload via GitHub Web:**
1. On your repo page → **Add file** → **Upload files**
2. Drag and drop all project files
3. Click **Commit changes**

### Step 3 — GitHub Actions Auto-Builds Your APK! 🎉

Once you push to GitHub, go to:
```
https://github.com/YOUR_USERNAME/HealthFit/actions
```

You'll see the workflow running. After ~5–8 minutes:
1. Click the completed workflow run
2. Scroll to **Artifacts** at the bottom
3. Download **HealthFit-Debug-APK** ← install this on your phone!

### Step 4 — Install APK on Your Android Phone

1. On your Android phone: **Settings → Security → Install unknown apps → Allow**
2. Download the APK from GitHub Actions artifacts
3. Open the APK file on your phone → Install
4. Done! HealthFit is installed 🎉

---

## 🏗️ Project Structure

```
HealthFit/
├── .github/workflows/
│   └── build-apk.yml          ← GitHub Actions CI/CD
├── app/src/main/
│   ├── java/com/healthfit/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   ├── entities/       ← BloodGlucose, BloodPressure, PhysicalActivity
│   │   │   ├── dao/            ← Database access objects
│   │   │   ├── AppDatabase.kt  ← Room database
│   │   │   └── HealthRepository.kt
│   │   ├── viewmodel/
│   │   │   └── HealthViewModel.kt
│   │   └── ui/
│   │       ├── dashboard/      ← Home screen
│   │       ├── glucose/        ← Glucose tracking
│   │       ├── bloodpressure/  ← BP tracking
│   │       ├── activity/       ← Physical activity
│   │       └── history/        ← Summary reports
│   └── res/
│       ├── layout/             ← All XML layouts
│       ├── navigation/         ← Nav graph
│       └── values/             ← Colors, strings, themes
├── build.gradle
└── settings.gradle
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary language |
| **Room (SQLite)** | Local database |
| **LiveData + ViewModel** | MVVM architecture |
| **Navigation Component** | Fragment navigation |
| **Material Design 3** | UI components |
| **GitHub Actions** | Auto-build APK on every push |

---

## 📊 Glucose Reference Ranges

| Type | Normal | Pre-Diabetic | Diabetic |
|---|---|---|---|
| Fasting | 70–100 mg/dL | 100–125 mg/dL | >126 mg/dL |
| Pre-meal | 70–130 mg/dL | — | >130 mg/dL |
| Post-meal (2hr) | <140 mg/dL | 140–199 mg/dL | >200 mg/dL |
| Bedtime | 100–140 mg/dL | — | — |

## 💓 Blood Pressure Categories (WHO)

| Category | Systolic | | Diastolic |
|---|---|---|---|
| Normal | <120 | and | <80 |
| Elevated | 120–129 | and | <80 |
| High Stage 1 | 130–139 | or | 80–89 |
| High Stage 2 | ≥140 | or | ≥90 |
| Crisis | >180 | or | >120 |

---

## 🔄 Trigger a New Build

Any push to `main` branch auto-triggers a new APK build. You can also:
1. Go to **Actions** tab on GitHub
2. Click **Build HealthFit APK**
3. Click **Run workflow** → **Run workflow**

---

## 📄 License

MIT License — Free to use and modify for personal or commercial projects.
