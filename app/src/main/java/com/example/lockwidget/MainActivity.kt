package com.example.lockwidget

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lockwidget.ShizukuHelper
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity(), Shizuku.OnRequestPermissionResultListener {

    private lateinit var statusText: TextView
    private lateinit var enableServiceButton: Button
    private lateinit var shizukuStatusText: TextView
    private lateinit var enableShizukuButton: Button

    private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == 1001 && grantResult == PackageManager.PERMISSION_GRANTED) {
            updateShizukuStatus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.status_text)
        enableServiceButton = findViewById(R.id.enable_service_button)
        shizukuStatusText = findViewById(R.id.shizuku_status_text)
        enableShizukuButton = findViewById(R.id.enable_shizuku_button)

        enableServiceButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        enableShizukuButton.setOnClickListener {
            if (!ShizukuHelper.isShizukuInitialized()) {
                try {
                    val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.manager")
                    if (intent != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Shizuku Manager not found.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error opening Shizuku Manager: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else if (!ShizukuHelper.isShizukuAvailable()) {
                ShizukuHelper.requestShizukuPermission(this, shizukuPermissionListener)
            }
        }

        Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
        updateShizukuStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = packageName + "/" + LockAccessibilityService::class.java.canonicalName
        val settingValue = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return settingValue?.let {
            TextUtils.SimpleStringSplitter(':').apply { setString(it) }.any { s -> s.equals(service, ignoreCase = true) }
        } ?: false
    }

    private fun updateServiceStatus() {
        if (isAccessibilityServiceEnabled()) {
            statusText.text = getString(R.string.service_status_enabled)
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            enableServiceButton.visibility = View.GONE
        } else {
            statusText.text = getString(R.string.service_status_disabled)
            statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            enableServiceButton.visibility = View.VISIBLE
        }
    }

    private fun updateShizukuStatus() {
        if (ShizukuHelper.isShizukuAvailable()) {
            shizukuStatusText.text = "Shizuku Available"
            shizukuStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            enableShizukuButton.visibility = View.GONE
        } else if (ShizukuHelper.isShizukuInitialized()) {
            shizukuStatusText.text = "Shizuku Running (No Permission)"
            shizukuStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
            enableShizukuButton.visibility = View.VISIBLE
        } else {
            shizukuStatusText.text = "Shizuku Not Available"
            shizukuStatusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            enableShizukuButton.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
    }
}