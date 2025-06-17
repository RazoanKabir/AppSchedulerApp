package com.example.appschedulerapp.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.Window
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appschedulerapp.R
import com.example.appschedulerapp.util.PrefConstants
import com.example.appschedulerapp.util.PrefConstants.Companion.KEY_BATTERY_DONE
import com.example.appschedulerapp.util.PrefConstants.Companion.KEY_OVERLAY_DONE
import com.example.appschedulerapp.util.PrefConstants.Companion.PREF_NAME
import com.example.appschedulerapp.util.UtilClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.processphoenix.ProcessPhoenix

class StartingActivity : AppCompatActivity() {

    private var dialog: Dialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.starting)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        if (!prefs.getBoolean(KEY_BATTERY_DONE, false)) {
            checkBatteryOptimization(prefs)
            return
        }

        val overlayGranted = Settings.canDrawOverlays(this)
        if (overlayGranted) {
            dialog?.dismiss()
            if (!prefs.getBoolean(KEY_OVERLAY_DONE, false)) {
                prefs.edit().putBoolean(KEY_OVERLAY_DONE, true).apply()
                proceedToMain()
            } else {
                proceedToMain()
            }
        } else if (dialog?.isShowing != true) {
            showOverlayDialog()
        }
    }

    private fun proceedToMain() {
        UtilClass.goToNextActivityByClearingHistory(this, MainActivity::class.java)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    @SuppressLint("BatteryLife")
    private fun checkBatteryOptimization(prefs: android.content.SharedPreferences) {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            AlertDialog.Builder(this)
                .setTitle("Allow Background Execution")
                .setMessage("To ensure scheduled apps run on time, allow this app to ignore battery optimizations.")
                .setCancelable(false)
                .setPositiveButton("Allow") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                    prefs.edit().putBoolean(KEY_BATTERY_DONE, true).apply()
                }
                .setNegativeButton("No Thanks") { _, _ ->
                    prefs.edit().putBoolean(KEY_BATTERY_DONE, true).apply()
                }
                .show()
        } else {
            prefs.edit().putBoolean(KEY_BATTERY_DONE, true).apply()
        }
    }

    private fun showOverlayDialog() {
        dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(R.layout.custom_delete_dialog)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            findViewById<TextView>(R.id.tvDialogTitle)?.text = getString(R.string.need_permission)
            findViewById<TextView>(R.id.tvDialogSubTitle)?.text = getString(R.string.need_overlay_permission)

            findViewById<FloatingActionButton>(R.id.fabNo)?.setOnClickListener {
                UtilClass.showToast(this@StartingActivity, getString(R.string.cantContinue))
            }

            findViewById<FloatingActionButton>(R.id.fabYes)?.setOnClickListener {
                val overlayIntent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(overlayIntent)
            }

            show()
        }
    }
}
