package com.jarvis.ai.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jarvis.ai.automation.AppLauncher
import com.jarvis.ai.automation.JarvisAccessibilityService
import org.json.JSONObject

data class ActionResult(
    val success: Boolean,
    val actionName: String,
    val message: String,
    val outputData: String? = null
)

class ActionRouter(private val context: Context) {

    private val appLauncher = AppLauncher(context)

    fun routeAction(actionName: String, args: JSONObject): ActionResult {
        return when (actionName) {
            "open_app" -> {
                val appName = args.optString("app_name")
                val success = appLauncher.launchAppByName(appName)
                ActionResult(
                    success = success,
                    actionName = actionName,
                    message = if (success) "Launched $appName." else "Could not find application $appName."
                )
            }

            "search_youtube" -> {
                val query = args.optString("query")
                val intent = Intent(Intent.ACTION_SEARCH).apply {
                    setPackage("com.google.android.youtube")
                    putExtra("query", query)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                return try {
                    context.startActivity(intent)
                    ActionResult(true, actionName, "Dispatched YouTube search for \"$query\".")
                } catch (e: Exception) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query"))
                    webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(webIntent)
                    ActionResult(true, actionName, "Opened YouTube search in browser for \"$query\".")
                }
            }

            "tap_element" -> {
                val targetText = args.optString("target_text")
                val service = JarvisAccessibilityService.instance
                if (service == null) {
                    return ActionResult(false, actionName, "Accessibility service not active. Please enable in Settings.")
                }
                val clicked = service.clickNodeByText(targetText)
                ActionResult(
                    success = clicked,
                    actionName = actionName,
                    message = if (clicked) "Tapped UI element \"$targetText\"." else "Could not locate visible element \"$targetText\"."
                )
            }

            "swipe_screen" -> {
                val direction = args.optString("direction", "up")
                val service = JarvisAccessibilityService.instance
                if (service == null) {
                    return ActionResult(false, actionName, "Accessibility service not active.")
                }
                val displayMetrics = context.resources.displayMetrics
                val midX = displayMetrics.widthPixels / 2f
                val midY = displayMetrics.heightPixels / 2f

                val (startX, startY, endX, endY) = when (direction.lowercase()) {
                    "up" -> listOf(midX, midY + 400f, midX, midY - 400f)
                    "down" -> listOf(midX, midY - 400f, midX, midY + 400f)
                    "left" -> listOf(midX + 300f, midY, midX - 300f, midY)
                    "right" -> listOf(midX - 300f, midY, midX + 300f, midY)
                    else -> listOf(midX, midY + 400f, midX, midY - 400f)
                }

                service.dispatchSwipe(startX, startY, endX, endY)
                ActionResult(true, actionName, "Dispatched swipe $direction gesture.")
            }

            "press_system_key" -> {
                val service = JarvisAccessibilityService.instance
                if (service == null) {
                    return ActionResult(false, actionName, "Accessibility service not active.")
                }
                val key = args.optString("key", "back")
                val actionCode = when (key.lowercase()) {
                    "home" -> AccessibilityService.GLOBAL_ACTION_HOME
                    "recents" -> AccessibilityService.GLOBAL_ACTION_RECENTS
                    "notifications" -> AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
                    else -> AccessibilityService.GLOBAL_ACTION_BACK
                }
                val ok = service.performSystemAction(actionCode)
                ActionResult(ok, actionName, "Dispatched global action $key.")
            }

            else -> ActionResult(false, actionName, "Unsupported action: $actionName")
        }
    }
}
