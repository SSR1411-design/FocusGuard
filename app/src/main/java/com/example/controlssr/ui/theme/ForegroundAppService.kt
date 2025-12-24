package com.example.controlssr

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class ForegroundAppService : AccessibilityService() {

    private var lastPackage: String? = null
    private var lastStartTime: Long = 0
    private var lastGoogleTimestamp: Long = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return

        // Ignore system noise
        // Ignore system noise + keyboards
        if (
            pkg.startsWith("com.android.systemui") ||
            pkg.startsWith("com.android.launcher") ||
            pkg.startsWith("com.oplus") ||
            pkg.startsWith("android") ||
            pkg.contains("inputmethod") ||        // keyboard events
            pkg.contains("keyboard") ||           // extra safety
            pkg == "com.google.android.inputmethod.latin" ||  // Gboard
            pkg == "com.oplus.inputmethod" ||     // Oppo keyboard
            pkg == "com.baidu.input" ||           // some phones
            pkg == "com.samsung.android.honeyboard" // Samsung keyboard
        ) return

        // Google overlay filter (ignore short popups)
        // Google Search = transition bridge
        if (pkg == "com.google.android.googlequicksearchbox") {

            val now = System.currentTimeMillis()

            // If we were tracking a real app, CLOSE it
            if (lastPackage != null && lastPackage != pkg) {
                val durationSec = ((now - lastStartTime) / 1000).toInt()

                Log.d("SSR_FOREGROUND", "User left $lastPackage after $durationSec sec (via Google bridge)")

                AppUsageStorage.saveSession(
                    context = this,
                    packageName = lastPackage!!,
                    durationSec = durationSec
                )
            }

            // DO NOT start tracking Google
            lastPackage = null
            lastStartTime = 0
            return
        }


        // First time app opened
        if (lastPackage == null) {
            lastPackage = pkg
            lastStartTime = System.currentTimeMillis()
            Log.d("SSR_FOREGROUND", "User opened: $pkg (tracking started)")
            return
        }

        // Switching to NEW app
        if (pkg != lastPackage) {
            val now = System.currentTimeMillis()
            val durationSec = ((now - lastStartTime) / 1000).toInt()

            Log.d("SSR_FOREGROUND", "User left $lastPackage after $durationSec sec")
            Log.d("SSR_FOREGROUND", "User opened: $pkg")

            // SAVE SESSION
            AppUsageStorage.saveSession(
                context = this,
                packageName = lastPackage!!,
                durationSec = durationSec
            )

            // Start tracking new app
            lastPackage = pkg
            lastStartTime = now
        }
    }

    override fun onInterrupt() {}
}
