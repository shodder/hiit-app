# HIIT Timer — Android

A native Android HIIT (High-Intensity Interval Training) timer app built with Kotlin and Jetpack Compose.

## Features

- **5 built-in workouts** — Tabata Classic, EMOM 10, Power HIIT, Core Blast, Upper Body
- **Custom workout builder** — create and edit workouts with a name, colour, sets, work/rest durations, and exercises
- **Exercise library** — 28 exercises to pick from, or enter your own
- **Weight tracking** — assign heavy/light weight labels to individual exercises
- **Circular progress timer** — animated ring showing time remaining in each phase
- **Countdown phase** — 3-second get-ready before work starts
- **Audio beeps** — countdown beeps at 3, 2, 1 seconds during each phase
- **Haptic feedback** — double pulse on work, single pulse on rest
- **Persistent storage** — custom workouts saved to device via SharedPreferences

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Navigation | Navigation Compose |
| State | ViewModel + StateFlow |
| Storage | SharedPreferences + Gson |
| Audio | SoundPool |
| Haptics | VibrationEffect |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |

## Project Structure

```
app/src/main/java/me/hodders/hitt/
├── MainActivity.kt
├── data/
│   ├── model/
│   │   └── Workout.kt          # Data models (Workout, Exercise, TimerPhase, WeightType)
│   └── WorkoutRepository.kt    # SharedPreferences persistence
├── utils/
│   ├── DefaultWorkouts.kt      # 5 built-in workouts + exercise library
│   └── FormatUtils.kt          # Time formatting helpers
└── ui/
    ├── theme/                  # Colours, typography, dark theme
    ├── navigation/
    │   └── AppNavigation.kt    # NavHost with home / timer / builder routes
    ├── components/
    │   └── CircularProgressTimer.kt
    └── screens/
        ├── home/               # Workout list screen
        ├── timer/              # Timer screen + ViewModel
        └── builder/            # Workout builder screen + ViewModel
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 35
- A device or emulator running Android 8.0+

### Build & Run

1. Clone the repo and open `hiit-android/` in Android Studio
2. Wait for the Gradle sync to complete (~2 min on first run)
3. Connect a device via USB with USB Debugging enabled, or start an emulator
4. Click **Run** (▶)

### Install APK directly

A pre-built debug APK can be built with:

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

Copy this to your phone and open it. Enable **Install from unknown sources** if prompted.

## Package

`me.hodders.hitt`

## Screens

| Screen | Route | Description |
|---|---|---|
| Home | `home` | Lists all workouts; FAB to create new |
| Timer | `timer/{workoutJson}` | Runs the workout with circular progress, phase labels, next-up box |
| Builder | `builder/{workoutJson}` | Form to create or edit a workout |

## Compose Previews

All screen files include `@Preview` annotations. Open any screen file in Android Studio and switch to **Split** view to see rendered previews without a device.
