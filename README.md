# FocusGuard 🧠📱  
An Intelligent Android App Usage Monitoring System

FocusGuard is an Android application designed to monitor real-time foreground app activity at the system level.  
The project focuses on **accurate session tracking**, **noise-free detection**, and **backend-first analytics**, forming a foundation for future ML-based digital wellbeing and addiction prevention systems.

---

## Features

- Real-time detection of foreground apps using **AccessibilityService**
- Session-based tracking of app usage
- Calculates:
  - Total time spent per app
  - Longest continuous usage session per app
- Filters out system noise:
  - Launchers
  - System UI
  - Keyboards
  - OEM overlays (OnePlus / Oppo)
- Persistent local storage of usage data
- Designed to be extensible for ML-driven behavior analysis

---

## Tech Stack

- **Language:** Kotlin  
- **Platform:** Android  
- **APIs Used:**  
  - AccessibilityService  
  - UsageStats API  
- **Tools:** Android Studio, Logcat  

---

## How It Works (High-Level)

1. An Accessibility Service listens for window state changes.
2. Foreground app transitions are detected and filtered.
3. Each app session is timed from entry to exit.
4. Usage statistics are stored locally.
5. The architecture supports future ML modules for:
   - Addiction pattern detection
   - Context-aware intervention logic

---

## Current Status

- Backend logic completed
- Accurate foreground detection implemented
- Data collection working across OEM-modified Android systems

UI/UX is intentionally minimal at this stage to prioritize system accuracy and data integrity.

---

## Future Scope

- ML-based behavior classification (productive vs addictive usage)
- Time-based intervention and app blocking logic
- Usage pattern visualization (daily / weekly stats)
- Federated or on-device learning for privacy-preserving intelligence

---

## Disclaimer

This app uses Android Accessibility Services strictly for usage monitoring and digital wellbeing research purposes.  
No personal data is transmitted off-device.

---

## Author

Built with interest and fun to try out new things  
by Suryansh Singh Rathore
