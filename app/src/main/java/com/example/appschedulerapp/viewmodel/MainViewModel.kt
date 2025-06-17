package com.example.appschedulerapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appschedulerapp.R
import com.example.appschedulerapp.adapter.ScheduledAppListAdapter
import com.example.appschedulerapp.handler.dbhandler.DatabaseHandler
import com.example.appschedulerapp.model.AppSelectionModel
import com.example.appschedulerapp.util.UtilClass
import com.example.appschedulerapp.util.ViewDialog

class MainViewModel : ViewModel() {
    fun deleteAll(context: Context) {
        val selectedAppList = getAppList(context)
        DatabaseHandler(context).deleteAll(selectedAppList)
    }

    fun deleteAllHistory(context: Context) {
        val appListHistory = DatabaseHandler(context).viewScheduledAppHistory()
        if (appListHistory.size == 0) {
            UtilClass.showToast(context, context.getString(R.string.noHistoryFound))
            return
        }
        val status = DatabaseHandler(context).deleteAllHistory(appListHistory)
        if (status != null) {
            if (status > -1) UtilClass.showToast(
                context,
                context.getString(R.string.successfullyDeletedAllHistory)
            )
        }
    }

    fun viewAppList(
        context: Context,
        rvAppList: RecyclerView,
        rlAppList: RelativeLayout,
        rvEmptyView: RelativeLayout,
        deletedApp: ViewDialog.DeletedApp
    ) {
        val selectedAppList = getAppList(context)
        if (selectedAppList.size > 0) {
            rvAppList.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
            rvAppList.setHasFixedSize(true)
            rvAppList.isNestedScrollingEnabled = false
            val scheduledAppListAdapter = ScheduledAppListAdapter(selectedAppList, deletedApp)
            rvAppList.adapter = scheduledAppListAdapter
            rlAppList.visibility = View.VISIBLE
        } else {
            rlAppList.visibility = View.GONE
            rvEmptyView.visibility = View.VISIBLE
        }
    }

    private fun getAppList(context: Context): ArrayList<AppSelectionModel> {
        return DatabaseHandler(context).viewScheduledApp()
    }
}