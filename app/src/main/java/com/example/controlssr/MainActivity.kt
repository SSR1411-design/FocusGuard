package com.example.controlssr

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.usage.UsageStatsManager
import android.util.Log
import android.content.Intent
import android.provider.Settings
import android.app.AppOpsManager
import android.os.Process
import android.app.usage.UsageEvents

class MainActivity : AppCompatActivity() {
    private var seconds = 0
    private var running = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startTimer()

        // Ask for permission if not granted
        if (!hasUsagePermission()) {
            Toast.makeText(this, "Permission needed!", Toast.LENGTH_LONG).show()
            requestUsagePermission()
        }

        val btnShow = findViewById<Button>(R.id.btnShowUsage)
        val txtData = findViewById<TextView>(R.id.txtUsageData)

        btnShow.setOnClickListener {
            txtData.text = AppUsageStorage.getAllUsageSummary(this)
        }
    }

    private fun startTimer() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                seconds++
                Log.d("SSR_TIMER", "Timer running: $seconds sec")

                handler.postDelayed(this, 1000)  // Run every 1 sec
            }
        })
    }

    private fun hasUsagePermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsagePermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun getForegroundApp(): String {
        val usm = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = end - 5000  // last 5 seconds

        val events = usm.queryEvents(start, end)
        val ev = UsageEvents.Event()

        var lastRealApp: String? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(ev)

            if (ev.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {

                val pkg = ev.packageName

                if (
                    pkg != packageName &&                       // ignore my app
                    !pkg.contains("launcher") &&                 // ignore launchers
                    pkg != "com.android.systemui" &&             // system UI
                    pkg != "com.google.android.gms" &&           // google services
                    !pkg.startsWith("com.coloros") &&            // oppo/realme stuff
                    !pkg.startsWith("com.android")               // generic system apps
                ) {
                    lastRealApp = pkg       // THIS is the real app
                }
            }
        }

        return lastRealApp?.let { "Foreground App: $it" }
            ?: "No foreground app found"
    }
}
