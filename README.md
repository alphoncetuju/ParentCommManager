# 📱 Parent Communication Manager
### For Teachers — Manage Parent Calls & SMS from One App

A fully offline Android app for teachers to manage communication with parents
via calls and SMS using the phone's own SIM card.

---

## ✅ Features
- **Learner Database** — Add, edit, delete, search learners with parent contacts
- **One-tap Calling** — Call any parent directly from the app
- **SMS Messaging** — Send personalised SMS with learner's first name auto-inserted
- **Bulk SMS** — Send to selected parents or ALL parents at once
- **Message Templates** — 6 ready-made templates, fully customisable
- **Call History** — Automatic log of every call (date, time, duration)
- **Message History** — All sent and received SMS stored by learner
- **Incoming SMS** — Parent replies automatically stored under their learner
- **CSV Export** — Export all learner records to CSV
- **Works 100% Offline** — No internet required, all data on your phone

---

## 🚀 HOW TO GET YOUR APK — 3 OPTIONS

### ✅ OPTION 1: GitHub Actions (FREE, No installs needed — RECOMMENDED)

**Step 1:** Create a free account at https://github.com  
**Step 2:** Create a new repository (click + → New repository → name it "ParentCommManager" → Create)  
**Step 3:** Upload the project ZIP contents to that repository  
**Step 4:** Go to your repo → **Actions** tab → you'll see "Build APK" workflow  
**Step 5:** Click **"Run workflow"** → **"Run workflow"** (green button)  
**Step 6:** Wait ~5 minutes → click the finished run → scroll down to **Artifacts**  
**Step 7:** Download **ParentCommManager-debug.zip** → extract → get the `.apk` file  
**Step 8:** Transfer APK to your Android phone via USB, WhatsApp, email, or Google Drive  
**Step 9:** On the phone: Settings → Security → **Enable "Install from Unknown Sources"**  
**Step 10:** Open the APK file on your phone → Install → Done! ✅

---

### ✅ OPTION 2: Android Studio (Local build — Windows/Mac/Linux)

1. Download Android Studio FREE from: https://developer.android.com/studio  
2. Install Android Studio (follow the setup wizard — it installs the SDK automatically)  
3. Open Android Studio → **"Open an existing project"** → select this folder  
4. Wait for Gradle sync to finish (first time takes ~5 minutes, downloads dependencies)  
5. Click the green **▶ Run** button OR go to **Build → Build Bundle(s)/APK(s) → Build APK(s)**  
6. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`  
7. Transfer to your phone and install

---

### ✅ OPTION 3: Command Line (Linux/Mac with Java installed)

```bash
# 1. Make sure Java 17 is installed:
java -version   # should show version 17 or higher

# 2. Navigate to project folder:
cd ParentCommManager

# 3. Run the build script:
chmod +x gradlew
./gradlew assembleDebug

# 4. APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📲 Installing on Your Android Phone

1. Transfer the APK file to your phone (USB / WhatsApp / email / Google Drive)
2. On your phone: **Settings** → **Security** (or Privacy) → Enable **"Install unknown apps"** or **"Unknown sources"**
3. Open a file manager on your phone → find the APK → tap it → **Install**
4. Open "Parent Comm Manager" from your app drawer
5. Grant all requested permissions (SMS, Phone, Contacts)

> **Works on:** Any Android phone running Android 5.0 (Lollipop) or higher

---

## 🔑 Permissions Explained

| Permission | Why Needed |
|---|---|
| CALL_PHONE | To call parents directly from the app |
| SEND_SMS | To send SMS messages to parents |
| RECEIVE_SMS | To capture incoming replies from parents |
| READ_SMS | To read incoming parent messages |
| READ_CALL_LOG | To record call duration automatically |
| WRITE_CALL_LOG | To save call records |

All data stays **on your phone only** — nothing is uploaded anywhere.

---

## 📁 Project Structure

```
ParentCommManager/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/teacherapp/parentcomm/
│   │   ├── activities/          ← All screens
│   │   │   ├── MainActivity.java
│   │   │   ├── AddEditLearnerActivity.java
│   │   │   ├── SendSmsActivity.java
│   │   │   ├── BulkSmsActivity.java
│   │   │   └── HistoryActivity.java
│   │   ├── adapters/            ← RecyclerView adapters
│   │   ├── database/            ← SQLite DatabaseHelper
│   │   ├── models/              ← Learner, CallLog, Message
│   │   ├── receivers/           ← SMS broadcast receiver
│   │   └── utils/               ← CallTracker, ExportUtils
│   └── res/                     ← Layouts, drawables, strings
├── .github/workflows/build.yml  ← Auto-build APK on GitHub
├── build_apk.sh                 ← Linux/Mac build script
├── build_apk_windows.bat        ← Windows build script
└── README.md                    ← This file
```

---

## 💡 Usage Guide

### Adding a Learner
1. Tap the **+** (orange) button on the main screen
2. Enter the learner's full name (first name is extracted automatically)
3. Enter the parent/guardian name and phone number
4. Optionally enter the class name
5. Tap **Save Learner**

### Calling a Parent
- On the learner card, tap the **green phone icon**
- Confirm the call in the dialog
- The app will log the call automatically when you return

### Sending SMS to One Parent
- Tap the **blue SMS icon** on any learner card
- Choose a template or type your own message
- `[Parent]` and `[FirstName]` are auto-replaced with real names
- Tap **Send SMS**

### Bulk SMS
- Tap **Bulk SMS** button (or the menu)
- Check boxes next to the parents you want to message
- OR tap **Send to All** to message every parent
- Type your message (use `[Parent]` and `[FirstName]` for personalisation)
- Confirm and send

### Viewing History
- Tap **History** button or the menu icon
- Switch between **All**, **Calls**, and **SMS** tabs
- Each record shows learner name, parent name, date/time, and content

---

*Built for teachers. Works offline. All data stays on your phone.*
