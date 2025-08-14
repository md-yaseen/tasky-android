# Tasky (Android) — Debug Build

This is a minimal Android task manager with:
- Daily recurring tasks (auto reset via WorkManager)
- Jetpack Compose UI
- Room persistence
- **Interactive home-screen widget (Glance)** to add/toggle tasks

## Build locally (recommended)
Open the project in **Android Studio (Koala or newer)** and run:
- **Build > Build APK(s)**

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions build (optional)
Add this workflow at `.github/workflows/build.yml` (included) and push to GitHub.
It uses Gradle Wrapper if present; otherwise open once in Android Studio to generate wrapper (`gradle wrapper`).
