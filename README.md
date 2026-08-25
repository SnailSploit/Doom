# The Magical World of Ben

A mobile, DOOM-style raycasting shooter. Play as Ben — a punk rocker blasting
through a magical world full of weird creatures. Built as a single
self-contained `index.html` and packaged into an installable Android APK.

## Play

- **In a browser:** open `index.html` on a phone (or desktop). Left side is a
  virtual joystick to move/strafe; right side swipes to look and taps to shoot.
  Desktop fallback: WASD + arrow keys + space.
- **On Android:** install the APK below.

## Install the APK

1. Download **`dist/magical-world-of-ben-debug.apk`**.
2. On your phone, allow installing from unknown sources for your browser/file
   manager.
3. Open the APK and install. Launch **Magical World of Ben** — it runs
   fullscreen in landscape.

This is a debug build (signed with the standard Android debug key), meant for
sideloading and testing.

## Creatures

- **Globkin** — a squishing gelatinous blob
- **Chomper** — a walking maw full of teeth
- **Float-Eye** — a floating eyeball trailing tentacles
- **Megabeast** — a hulking humanoid with a glowing core (boss)

## Weapons

- **Pick Gun** (pistol) · **Bass Blaster** (shotgun) · **Shredder** (plasma)

## Build the APK yourself

Requires a JDK (17+) and the Android SDK (platform 34, build-tools 34.0.0).

```bash
cd android
ANDROID_HOME=/path/to/android-sdk ./gradlew assembleDebug
# output: app/build/outputs/apk/debug/app-debug.apk
```

The Gradle wrapper (8.5) is included. `android/app/src/main/assets/index.html`
is the bundled copy of the game — if you edit the root `index.html`, copy it
into that path before rebuilding.

## Project layout

```
index.html                         # the game (browser-playable)
dist/                              # prebuilt APK
android/                           # native WebView wrapper (Gradle project)
  app/src/main/assets/index.html   # game bundled into the APK
  app/src/main/java/.../MainActivity.java
```
