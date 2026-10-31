package com.jarvis.ai.automation

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * JarvisAccessibilityService
 * Core native Android service for accessible UI interaction, node inspection, and gesture automation.
 */
class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "JarvisAccessibility"
        var instance: JarvisAccessibilityService? = null
            private set
        
        @Volatile
        var isAutomationActive: Boolean = false
            private set

        fun stopAutomation() {
            isAutomationActive = false
            Log.d(TAG, "Emergency Stop Automation invoked.")
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isAutomationActive = true
        Log.i(TAG, "JARVIS Accessibility Service successfully connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Monitors window state transitions and UI focus changes
    }

    override fun onInterrupt() {
        Log.w(TAG, "JARVIS Accessibility Service interrupted.")
        isAutomationActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        isAutomationActive = false
        Log.i(TAG, "JARVIS Accessibility Service destroyed.")
    }

    /**
     * Traverses accessibility tree to locate an interactive element by exact or partial text
     */
    fun findNodeByText(text: String): AccessibilityNodeInfo? {
        val root = rootInActiveWindow ?: return null
        val matchedNodes = root.findAccessibilityNodeInfosByText(text)
        return matchedNodes.firstOrNull { it.isClickable || it.parent?.isClickable == true }
            ?: matchedNodes.firstOrNull()
    }

    /**
     * Clicks an accessible UI node matching the given text or description
     */
    fun clickNodeByText(text: String): Boolean {
        if (!isAutomationActive) return false
        val node = findNodeByText(text) ?: return false
        return if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            node.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
        }
    }

    /**
     * Dispatches a tap gesture at specific display coordinates
     */
    fun dispatchTap(x: Float, y: Float, callback: ((Boolean) -> Unit)? = null) {
        if (!isAutomationActive) {
            callback?.invoke(false)
            return
        }

        val path = Path().apply {
            moveTo(x, y)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                callback?.invoke(true)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                callback?.invoke(false)
            }
        }, null)
    }

    /**
     * Dispatches a swipe gesture (e.g. scroll up, down, left, right)
     */
    fun dispatchSwipe(startX: Float, startY: Float, endX: Float, endY: Float, durationMs: Long = 300, callback: ((Boolean) -> Unit)? = null) {
        if (!isAutomationActive) {
            callback?.invoke(false)
            return
        }

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                callback?.invoke(true)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                callback?.invoke(false)
            }
        }, null)
    }

    /**
     * Dispatches global system navigation keys
     */
    fun performSystemAction(action: Int): Boolean {
        return performGlobalAction(action)
    }
}
