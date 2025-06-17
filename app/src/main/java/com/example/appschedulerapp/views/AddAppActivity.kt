package com.example.appschedulerapp.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.appschedulerapp.R
import com.example.appschedulerapp.databinding.ActivityAddAppBinding
import com.example.appschedulerapp.handler.dbhandler.DatabaseHandler
import com.example.appschedulerapp.model.AppSelectionModel
import com.example.appschedulerapp.util.Constants
import com.example.appschedulerapp.util.UtilClass
import com.example.appschedulerapp.util.UtilClass.Companion.getDate
import com.example.appschedulerapp.util.UtilClass.Companion.getTime
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class AddAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAppBinding
    private var appName: String? = null
    private var appPackageName: String? = null
    private var appIcon: Drawable? = null
    private var cal = Calendar.getInstance()
    private var minSelected = 0
    private var secSelected = 30
    private var hourSelected = 0
    private var daySelected = 0
    private var monthSelected = 0
    private var yearSelected = 0
    private var app: AppSelectionModel? = null
    private var from: String? = Constants.from
    private var appId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDateValues()
        checkIntent()
        initListener()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateValues() {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvDate.text = df.format(cal.time)
        binding.tvTime.text = SimpleDateFormat("hh:mm:ss aa").format(cal.time)
        yearSelected = cal.get(Calendar.YEAR)
        monthSelected = cal.get(Calendar.MONTH)
        daySelected = cal.get(Calendar.DAY_OF_MONTH)
        hourSelected = cal.get(Calendar.HOUR_OF_DAY)
        minSelected = cal.get(Calendar.MINUTE)
        secSelected = cal.get(Calendar.SECOND)
    }

    private fun checkIntent() {
        intent?.let {
            appName = it.getStringExtra(Constants.appName)
            appPackageName = it.getStringExtra(Constants.appPackageName)
            appName?.let { name -> setAppInfoFromSelection(name, appPackageName) }

            if (it.getStringExtra(Constants.appToJSON) != null) {
                app = Gson().fromJson(it.getStringExtra(Constants.appToJSON), AppSelectionModel::class.java)
                appId = app?.id.toString()
                setAppInfoForEdit(app)
            }

            appId = it.getStringExtra(Constants.appId) ?: appId

            if (it.getStringExtra(Constants.from) == Constants.edit) {
                from = Constants.edit
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initListener() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            binding.tvTime.text = SimpleDateFormat("hh:mm:ss aa").format(cal.time)
            hourSelected = hour
            minSelected = minute
        }

        binding.llSelectTime.setOnClickListener {
            TimePickerDialog(
                this, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), false
            ).show()
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            cal.set(year, month, day)
            binding.tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.time)
            yearSelected = year
            monthSelected = month
            daySelected = day
        }

        binding.llSelectDate.setOnClickListener {
            cal = Calendar.getInstance()
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = cal.timeInMillis
                show()
            }
        }

        binding.rlGoToAppList.setOnClickListener { goToAppList() }
        binding.rlSelectApp.setOnClickListener { goToAppList() }
        binding.btnScheduleApp.setOnClickListener { checkIfSetInFuture() }
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setAppInfoFromSelection(appName: String?, appPackageName: String?) {
        binding.rlGoToAppList.visibility = View.GONE
        try {
            appIcon = packageManager.getApplicationIcon(appPackageName!!)
            binding.ivSelectedAppIcon.setImageDrawable(appIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        binding.tvAppName.text = appName
        binding.tvPackageName.text = appPackageName
        binding.ivScheduleIconStatus.setImageDrawable(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_alarm_on_24, null)
        )
        binding.rlSelectApp.visibility = View.VISIBLE
    }

    @SuppressLint("SimpleDateFormat")
    private fun setAppInfoForEdit(app: AppSelectionModel?) {
        setAppInfoFromSelection(app?.appName, app?.appPackageName)
        binding.tvTime.text = getTime(app?.dateTime)
        binding.tvDate.text = getDate(app?.dateTime)
        yearSelected = app?.year?.toIntOrNull() ?: 0
        monthSelected = app?.month?.toIntOrNull() ?: 0
        daySelected = app?.day?.toIntOrNull() ?: 0
        hourSelected = app?.hour?.toIntOrNull() ?: 0
        minSelected = app?.minute?.toIntOrNull() ?: 0
        secSelected = app?.second?.toIntOrNull() ?: 0
        binding.etNote.setText(app?.note)
    }

    private fun checkIfSetInFuture() {
        val inputDate = "$yearSelected-${monthSelected + 1}-$daySelected $hourSelected:$minSelected:$secSelected"
        if (UtilClass.checkIfSetInFuture(this, inputDate)) {
            if (from == Constants.edit) updateApp() else addApp()
        }
    }

    private fun goToAppList() {
        val b = Bundle().apply {
            putString(Constants.from, from)
            putString(Constants.appId, app?.id.toString())
        }
        UtilClass.goToNextActivityWithBundleWithoutClearing(this, b, AppListActivity::class.java)
    }

    private fun addApp() {
        if (binding.tvAppName.text.isNullOrEmpty() || binding.tvPackageName.text.isNullOrEmpty()) {
            UtilClass.showToast(this, getString(R.string.pleaseSelectApp))
            return
        }
        val status = DatabaseHandler(this).addApp(generateModel(0), this)
        status?.let { updateStatus(it) }
    }

    private fun updateApp() {
        if (binding.tvAppName.text.isNullOrEmpty() || binding.tvPackageName.text.isNullOrEmpty()) {
            UtilClass.showToast(this, getString(R.string.pleaseSelectApp))
            return
        }
        val status = DatabaseHandler(this).updateSchedule(generateModel(appId?.toInt()), this)
        status?.let { updateStatus(it) }
    }

    private fun generateModel(id: Int?): AppSelectionModel {
        return AppSelectionModel(
            id,
            binding.tvAppName.text.toString(),
            binding.tvPackageName.text.toString(),
            binding.etNote.text.toString(),
            "${binding.tvDate.text} ${binding.tvTime.text}",
            yearSelected.toString(),
            monthSelected.toString(),
            daySelected.toString(),
            hourSelected.toString(),
            minSelected.toString(),
            secSelected.toString(),
            "0",
            "0"
        )
    }

    private fun updateStatus(status: Long) {
        if (status > -1) {
            UtilClass.showToast(this, getString(R.string.recordSaved))
            DatabaseHandler(this).setLatestScheduledApp(this)
            UtilClass.goToNextActivity(this, MainActivity::class.java)
            finish()
        } else {
            UtilClass.showToast(this, getString(R.string.canNotSavedRecord))
        }
    }

    private fun updateStatus(status: Int) {
        if (status > -1) {
            UtilClass.showToast(this, getString(R.string.recordUpdated))
            DatabaseHandler(this).setLatestScheduledApp(this)
            UtilClass.goToNextActivity(this, MainActivity::class.java)
            finish()
        } else {
            UtilClass.showToast(this, getString(R.string.canNotUpdateRecord))
        }
    }
}
