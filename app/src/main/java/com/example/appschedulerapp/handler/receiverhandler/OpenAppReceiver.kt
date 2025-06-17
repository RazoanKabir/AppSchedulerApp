package com.example.appschedulerapp.handler.receiverhandler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.appschedulerapp.R
import com.example.appschedulerapp.handler.dbhandler.DatabaseHandler
import com.example.appschedulerapp.model.AppSelectionModel
import com.example.appschedulerapp.service.StickyService
import com.example.appschedulerapp.util.Constants
import com.example.appschedulerapp.util.UtilClass
import com.example.appschedulerapp.views.MainActivity
import com.google.gson.Gson

class OpenAppReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val app = Gson().fromJson<AppSelectionModel>(
            intent.getStringExtra(Constants.appToJSON),
            AppSelectionModel::class.java
        )

        val pm: PackageManager = context.packageManager
        val isInstalled = isPackageInstalled(app.appPackageName!!, pm)

        if (isInstalled) {
            launchNotification(context, app)

            val launchIntent = pm.getLaunchIntentForPackage(app.appPackageName)
            if (launchIntent != null) {
                context.startActivity(launchIntent)
                updateLatestAppToOpen(context, app)
            }
        } else {
            UtilClass.showToast(context, "App not installed!")
        }
    }

    private fun launchNotification(context: Context, app: AppSelectionModel) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "AppScheduler"
        val description = "App scheduler open"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, description, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("${app.appName} was scheduled")
            .setContentText("${app.appName} was scheduled to open at ${app.dateTime}")
            .setSmallIcon(R.drawable.ic_splash)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_splash))
            .setContentIntent(pendingIntent)

        notificationManager.notify(UtilClass.getRandom(), builder.build())
    }

    private fun updateLatestAppToOpen(context: Context, app: AppSelectionModel) {
        val db = DatabaseHandler(context)
        var status = db.addAppHistory(
            AppSelectionModel(
                app.id,
                app.appName,
                app.appPackageName,
                app.note,
                app.dateTime,
                app.year,
                app.month,
                app.day,
                app.hour,
                app.minute,
                app.second,
                "0", // not repeatable
                "1"  // executed
            )
        )

        if (status != null && status > -1) {
            status = db.deleteSchedule(app.id, context)?.toLong()
            if (status != null && status > -1) {
                db.setLatestScheduledApp(context)
                val serviceIntent = Intent(context, StickyService::class.java)
                try {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } catch (e: SecurityException) {
                    UtilClass.showToast(context, "Service start failed: ${e.localizedMessage}")
                }

            }
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
