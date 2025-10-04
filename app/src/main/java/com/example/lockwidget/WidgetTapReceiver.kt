package com.example.lockwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.Toast

class WidgetTapReceiver : BroadcastReceiver() {

    companion object {
        private var lastTapTime: Long = 0
        private const val DOUBLE_TAP_DELAY: Long = 400 // ms
    }

    override fun onReceive(context: Context, intent: Intent) {
        val currentTime = SystemClock.uptimeMillis()

        if (currentTime - lastTapTime < DOUBLE_TAP_DELAY) {
            lastTapTime = 0
            lockScreen(context)
        } else {
            lastTapTime = currentTime
        }
    }

    private fun lockScreen(context: Context) {
        if (ShizukuHelper.isShizukuAvailable()) {
            if (ShizukuHelper.lockScreen(context)) {
                return
            }
        }

        val service = LockAccessibilityService.instance
        if (service != null && service.isServiceEnabled()) {
            service.lockScreen()
            return
        }

        Toast.makeText(context, "Please enable Accessibility Service or Shizuku", Toast.LENGTH_LONG).show()
        val appIntent = Intent(context, MainActivity::class.java)
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)
    }
}