package com.example.appschedulerapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.example.appschedulerapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UtilClass {
    companion object {
        fun goToNextActivity(context: Context, targetActivity: Class<*>?) {
            val `in` = Intent(context, targetActivity)
            context.startActivity(`in`)
        }

        fun goToNextActivityByClearingHistory(
            context: Context,
            targetActivity: Class<out Activity?>?
        ) {
            val `in` = Intent(context, targetActivity)
            `in`.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(`in`)
        }

        fun goToPreviousActivityWithBundle(
            context: Context,
            bundle: Bundle?,
            targetActivity: Class<out Activity?>?
        ) {
            (context as Activity).finish()
            val intent = Intent(context, targetActivity)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtras(bundle!!)
            context.startActivity(intent)
        }

        fun goToNextActivityWithBundleWithoutClearing(
            context: Context,
            bundle: Bundle?,
            targetActivity: Class<out Activity?>?
        ) {
            val `in` = Intent(context, targetActivity)
            `in`.putExtras(bundle!!)
            context.startActivity(`in`)
        }

        fun showToast(context: Context?, msg: String?) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }

        fun getRandom(): Int {
            val min = 1
            val max = 1000
            return Random().nextInt(max - min + 1) + min
        }

        @SuppressLint("SimpleDateFormat")
        fun checkIfSetInFuture(context: Context, old: String): Boolean {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val getCurrentDateTime = sdf.format(c.time)
            val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val date: Date = inputFormat.parse(old)
            val getMyTime = sdf.format(date)
            var check = false
            if (getCurrentDateTime < getMyTime) {
                check = true
            } else {
                showToast(context, context.getString(R.string.pleaseSelectAFutureDateTime))
            }
            return check
        }

        fun getTime(s: String?): String {
            val inputFormatTime: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa", Locale.US)
            val time: Date = inputFormatTime.parse(s)
            val setT = SimpleDateFormat("hh:mm:ss aa", Locale.US).format(time)
            return setT
        }

        fun getDate(s: String?): String {
            val inputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa", Locale.US)
            val date: Date = inputFormat.parse(s)
            val setD = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date)
            return setD
        }
    }
}