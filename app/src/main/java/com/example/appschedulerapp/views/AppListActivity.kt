package com.example.appschedulerapp.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appschedulerapp.adapter.AppListAdapter
import com.example.appschedulerapp.databinding.ActivityAppListBinding
import com.example.appschedulerapp.model.PackageAppInfo
import com.example.appschedulerapp.util.Constants
import com.example.appschedulerapp.util.UtilClass
import com.example.appschedulerapp.viewmodel.AppListViewModel

class AppListActivity : AppCompatActivity(), AppListAdapter.SelectedApp {

    private lateinit var binding: ActivityAppListBinding
    private lateinit var appListVm: AppListViewModel
    private var appListAdapter: AppListAdapter? = null
    private var from: String? = ""
    private var appId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        initView()
        setObserver()
        initListener()
    }

    private fun getIntentData() {
        intent?.let {
            from = it.getStringExtra(Constants.from)
            appId = it.getStringExtra(Constants.appId)
        }
    }

    private fun initView() {
        appListVm = ViewModelProvider(this)[AppListViewModel::class.java]
        appListVm.execute(this)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setObserver() {
        appListVm.appsListLiveData.observe(this) { appList ->
            if (appList.isNotEmpty()) {
                with(binding) {
                    rvAppList.apply {
                        layoutManager = LinearLayoutManager(this@AppListActivity)
                        setHasFixedSize(true)
                        isNestedScrollingEnabled = false
                        adapter = AppListAdapter(appList, this@AppListActivity)
                    }
                    pBar.visibility = View.GONE
                    rlAppList.visibility = View.VISIBLE
                    appBarLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun appSelected(selectedApp: PackageAppInfo) {
        val bundle = Bundle().apply {
            putString(Constants.appName, selectedApp.appName)
            putString(Constants.appPackageName, selectedApp.packageName)
            putString(Constants.from, from)
            putString(Constants.appId, appId)
        }
        UtilClass.goToPreviousActivityWithBundle(this, bundle, AddAppActivity::class.java)
    }
}
