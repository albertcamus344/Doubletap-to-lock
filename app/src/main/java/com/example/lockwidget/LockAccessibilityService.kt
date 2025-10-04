package com.example.lockwidget

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class LockAccessibilityService : AccessibilityService() {

    companion object {
        var instance: LockAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed
    }

    override fun onInterrupt() {
        // Not needed
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun isServiceEnabled(): Boolean {
        return instance != null
    }

    fun lockScreen() {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    }
}