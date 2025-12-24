package com.example.controlssr

import android.content.Context

object AppUsageStorage {

    private const val PREF_NAME = "app_usage_storage"

    fun saveSession(
        context: Context,
        packageName: String,
        durationSec: Int
    ) {
        if (durationSec <= 0) return

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val totalKey = "${packageName}_total"
        val maxKey = "${packageName}_max"

        val currentTotal = prefs.getInt(totalKey, 0)
        val currentMax = prefs.getInt(maxKey, 0)

        prefs.edit()
            .putInt(totalKey, currentTotal + durationSec)
            .putInt(maxKey, maxOf(currentMax, durationSec))
            .apply()
    }

    fun getAllUsageSummary(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val all = prefs.all

        val result = StringBuilder()

        val packages = all.keys
            .filter { it.endsWith("_total") }
            .map { it.removeSuffix("_total") }
            .sorted()

        if (packages.isEmpty()) {
            return "No usage data yet"
        }

        for (pkg in packages) {
            val total = prefs.getInt("${pkg}_total", 0)
            val max = prefs.getInt("${pkg}_max", 0)

            result.append("📱 ").append(pkg).append("\n")
            result.append("   ⏱ Total: ").append(formatTime(total)).append("\n")
            result.append("   🔥 Longest: ").append(formatTime(max)).append("\n\n")
        }

        return result.toString()
    }

    private fun formatTime(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60

        return when {
            h > 0 -> "${h}h ${m}m"
            m > 0 -> "${m}m ${s}s"
            else -> "${s}s"
        }
    }
}
