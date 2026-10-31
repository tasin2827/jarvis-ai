package com.jarvis.ai.automation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class AppLauncher(private val context: Context) {

    companion object {
        private const val TAG = "AppLauncher"
        val KNOWN_PACKAGES = mapOf(
            "youtube" to "com.google.android.youtube",
            "chrome" to "com.android.chrome",
            "camera" to "com.android.camera",
            "gallery" to "com.google.android.apps.photos",
            "calculator" to "com.google.android.calculator",
            "settings" to "android.settings.SETTINGS",
            "music" to "com.google.android.apps.youtube.music",
            "spotify" to "com.spotify.music"
        )
    }

    fun launchAppByName(name: String): Boolean {
        val normalized = name.lowercase().trim()
        val pkg = KNOWN_PACKAGES[normalized] ?: findPackageByLabel(name)

        if (pkg != null) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return true
            }
        }

        // Generic fallback search via market or web
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=$name"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch $name", e)
            false
        }
    }

    private fun findPackageByLabel(label: String): String? {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        return apps.firstOrNull {
            pm.getApplicationLabel(it).toString().equals(label, ignoreCase = true)
        }?.packageName
    }
}
