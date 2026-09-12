package com.agenticandroid.app.agent

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityNodeInfo

class AccessibilityActionService : AccessibilityService() {
    override fun onServiceConnected() {
        instance = this
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) = Unit

    override fun onInterrupt() {
        instance = null
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    private fun perform(action: Action): ActionExecutionResult {
        return when (action.type) {
            ActionType.BACK -> ActionExecutionResult(
                performGlobalAction(GLOBAL_ACTION_BACK),
                "Back navigation requested"
            )
            ActionType.HOME -> ActionExecutionResult(
                performGlobalAction(GLOBAL_ACTION_HOME),
                "Home navigation requested"
            )
            ActionType.RECENTS -> ActionExecutionResult(
                performGlobalAction(GLOBAL_ACTION_RECENTS),
                "Recents navigation requested"
            )
            ActionType.TYPE_TEXT -> typeText(action.text.orEmpty())
            ActionType.CLICK -> click(action.x ?: 0f, action.y ?: 0f)
            ActionType.LONG_CLICK -> longClick(action.x ?: 0f, action.y ?: 0f)
            ActionType.SWIPE -> swipe(action)
            ActionType.SCROLL -> scroll(action.direction)
            ActionType.OPEN_APP -> openApp(action.packageName)
            ActionType.WAIT, ActionType.ASK_USER, ActionType.FINISH ->
                ActionExecutionResult(true, action.description)
            else -> ActionExecutionResult(false, "Unsupported action: ${action.type}")
        }
    }

    private fun typeText(text: String): ActionExecutionResult {
        val node = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            ?: return ActionExecutionResult(false, "No focused input field")
        val arguments = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        return ActionExecutionResult(
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments),
            "Text input requested"
        )
    }

    private fun click(x: Float, y: Float): ActionExecutionResult {
        return dispatchTap(x, y, 80L, "Tap gesture requested")
    }

    private fun longClick(x: Float, y: Float): ActionExecutionResult {
        return dispatchTap(x, y, 700L, "Long press gesture requested")
    }

    private fun dispatchTap(x: Float, y: Float, durationMs: Long, message: String): ActionExecutionResult {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, durationMs))
            .build()
        return ActionExecutionResult(
            dispatchGesture(gesture, null, null),
            message
        )
    }

    private fun swipe(action: Action): ActionExecutionResult {
        val startX = action.x ?: 540f
        val startY = action.y ?: 1200f
        val endY = if (action.direction.equals("up", ignoreCase = true)) 400f else 1800f
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(startX, endY)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, action.durationMs ?: 350L))
            .build()
        return ActionExecutionResult(
            dispatchGesture(gesture, null, null),
            "Swipe gesture requested"
        )
    }

    private fun scroll(direction: String?): ActionExecutionResult {
        val root = rootInActiveWindow ?: return ActionExecutionResult(false, "No active screen")
        val scrollable = findScrollable(root)
            ?: return ActionExecutionResult(false, "No scrollable node found")
        val action = if (direction == "up") {
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        } else {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        }
        return ActionExecutionResult(scrollable.performAction(action), "Scroll requested")
    }

    private fun findScrollable(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isScrollable) return node
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            val result = findScrollable(child)
            if (result != null) return result
        }
        return null
    }

    private fun openApp(packageName: String?): ActionExecutionResult {
        val target = packageName ?: return ActionExecutionResult(false, "No package name provided")
        val intent = packageManager.getLaunchIntentForPackage(target)
            ?: return ActionExecutionResult(false, "App not found: $target")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return ActionExecutionResult(true, "Opened $target")
    }

    companion object {
        @Volatile
        private var instance: AccessibilityActionService? = null

        fun execute(action: Action): ActionExecutionResult {
            return instance?.perform(action)
                ?: ActionExecutionResult(false, "Accessibility service is not enabled")
        }

        fun settingsIntent(service: android.content.Context): Intent =
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }
}