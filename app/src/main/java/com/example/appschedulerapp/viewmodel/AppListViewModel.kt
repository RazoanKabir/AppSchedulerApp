package com.example.appschedulerapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appschedulerapp.model.PackageAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListViewModel : ViewModel() {
    val appsListLiveData = MutableLiveData<ArrayList<PackageAppInfo>>()
    fun execute(context: Context) = viewModelScope.launch {
        val result = doInBackground(context)
        onPostExecute(result)
    }

    private suspend fun doInBackground(context: Context): ArrayList<PackageAppInfo> =
        withContext(Dispatchers.IO) {
            val apps: ArrayList<PackageAppInfo> = getPackages(context)
            delay(1000)
            return@withContext apps
        }

    private fun onPostExecute(apps: ArrayList<PackageAppInfo>) {
        appsListLiveData.value = apps
    }

    private fun getPackages(context: Context): ArrayList<PackageAppInfo> {
        val apps: ArrayList<PackageAppInfo> = getInstalledApps(context)
        val max = apps.size
        for (i in 0 until max) {
            apps[i]
        }
        return apps
    }

    private fun getInstalledApps(context: Context): ArrayList<PackageAppInfo> {
        val pm = context.packageManager
        val list = ArrayList<PackageAppInfo>()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val apps = pm.queryIntentActivities(intent, 0)

        for (resolveInfo in apps) {
            val appInfo = PackageAppInfo()
            appInfo.appName = resolveInfo.loadLabel(pm).toString()
            appInfo.packageName = resolveInfo.activityInfo.packageName
            appInfo.icon = resolveInfo.activityInfo.loadIcon(pm)
            list.add(appInfo)
        }
        return list
    }

}