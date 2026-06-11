# M400 YouTube Companion

Cast YouTube videos from your phone to Vuzix M400 smart glasses — like sending to a smart TV.

Two Kotlin apps:

| App | Installs on | What it does |
|---|---|---|
| **M400 Cast** (`phone/`) | Your phone (e.g. Galaxy Fold 5) | Appears in the YouTube app's **Share** menu as *"Play on M400"*. Also has a paste-a-link field as backup. |
| **M400 YouTube** (`glasses/`) | Vuzix M400 | Receives the link over the Vuzix Connectivity framework and plays the video full-screen in a browser view. Sign in to your Google account once — your YouTube Premium (no ads) is used. |

The phone → glasses link uses the official [Vuzix Connectivity SDK](https://github.com/Vuzix/connectivity-sdk), the same secure channel the Vuzix Companion App uses. No Wi-Fi setup, no IP addresses.

---

## 1. Get the APKs (no Android Studio needed)

Every push to this repository automatically builds both APKs with GitHub Actions:

1. Go to the repo's **Actions** tab → click the latest **Build APKs** run.
2. Under **Artifacts**, download **phone-apk** and **glasses-apk** (each is a zip containing the APK).

To trigger a fresh build manually: **Actions → Build APKs → Run workflow**.

<details>
<summary>Building locally instead (optional)</summary>

Install JDK 17 and the Android SDK (or just Android Studio), then:

```bash
./gradlew assembleDebug
# phone/build/outputs/apk/debug/phone-debug.apk
# glasses/build/outputs/apk/debug/glasses-debug.apk
```
</details>

## 2. One-time setup

### Prerequisite: Vuzix Companion App
The Connectivity framework needs the **Vuzix Companion App** ([Play Store](https://play.google.com/store/apps/details?id=com.vuzix.companion)) installed on your phone and **paired with your M400** (the app walks you through scanning a QR code on the glasses). If you already use it, you're set.

### Install the phone app
Copy `phone-debug.apk` to your phone and open it (allow "install unknown apps"), or use adb:

```bash
adb install phone-debug.apk
```

### Install the glasses app
Connect the M400 over USB with ADB enabled (on the M400: Settings → System → About → tap Build number 7× → Developer options → USB debugging):

```bash
adb install glasses-debug.apk
```

### First run on the M400
1. Open **M400 YouTube** on the glasses.
2. Press **"Allow auto-open"** and enable *Display over other apps* — this lets videos sent from your phone open automatically even when the app is closed. (Alternative via adb: `adb shell appops set com.m400companion.glasses SYSTEM_ALERT_WINDOW allow`)
3. Press **"Open YouTube / Sign in"** and log in to your Google account. The login is remembered, so YouTube Premium (no ads) works from then on.

## 3. Daily use

1. On your phone, find a video in the **YouTube app**.
2. **Share → "Play on M400"**.
3. The video starts playing full-screen on the glasses. 🎉

**Backup mode:** open *M400 Cast* on the phone, paste any YouTube link, press **Send to M400**. Supports `youtu.be`, `watch?v=`, Shorts, and live links, and keeps the timestamp (`t=`) if the link has one.

## 4. Troubleshooting

- **"Vuzix Companion app is not installed"** — install it from the Play Store and pair with the M400.
- **"Paired but not connected"** — make sure the glasses are on and the Companion app shows a green/connected state.
- **Video doesn't open automatically on the glasses** — grant the *Display over other apps* permission (step "First run" above), or leave the M400 YouTube app open in the foreground.
- **Google won't let you sign in on the glasses** — fully close the app and retry; the app already masks the WebView user agent which resolves the usual "browser not secure" block.
- **Ads appear** — you're not signed in on the glasses (Premium is per-account). Use "Open YouTube / Sign in" on the glasses app.

## Project layout

```
phone/    Android app for the phone (share target + URL field, sends via Connectivity SDK)
glasses/  Android app for the M400 (BroadcastReceiver + full-screen WebView player)
```

Both apps are plain Kotlin, minSdk 26, built with Gradle 8.7 / AGP 8.5.
