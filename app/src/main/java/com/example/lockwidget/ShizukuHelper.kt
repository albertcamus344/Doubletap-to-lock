package com.example.lockwidget

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener
import rikka.shizuku.SystemServiceHelper

class ShizukuHelper {

    companion object {
        private const val TAG = "ShizukuHelper"

        fun isShizukuInitialized(): Boolean {
            return try {
                Shizuku.pingBinder()
            } catch (e: Exception) {
                false
            }
        }

        fun isShizukuAvailable(): Boolean {
            if (!isShizukuInitialized()) {
                return false
            }

            if (Shizuku.isPreV11()) {
                return false
            }

            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }

        fun requestShizukuPermission(context: Context, listener: OnRequestPermissionResultListener) {
            if (isShizukuInitialized() && Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                Shizuku.addRequestPermissionResultListener(listener)
                Shizuku.requestPermission(1001)
            }
        }

        fun lockScreen(context: Context): Boolean {
            if (!isShizukuAvailable()) {
                Log.e(TAG, "Shizuku not available")
                return false
            }

            return try {
                Log.d(TAG, "Attempting to lock screen via Shizuku")

                val process = Shizuku.newProcess(arrayOf("input", "keyevent", "26"), null, null)
                val exitCode = process.waitFor()

                if (exitCode == 0) {
                    Log.d(TAG, "Screen lock command sent via Shizuku")
                    true
                } else {
                    Log.d(TAG, "Shizuku command failed with exit code: $exitCode")
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to lock screen via Shizuku", e)
                false
            }
        }
    }
}