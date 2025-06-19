package com.example.appschedulerapp.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var fullAppList = listOf<PackageAppInfo>()
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

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filteredList = if (query.isEmpty()) {
                    appListVm.appsListLiveData.value ?: emptyList()
                } else {
                    appListVm.appsListLiveData.value?.filter {
                        it.appName?.contains(query, ignoreCase = true) == true
                    } ?: emptyList()
                }
                appListAdapter?.updateList(filteredList)
            }
        })
    }




    private fun setObserver() {
        appListVm.appsListLiveData.observe(this) { appList ->
            if (appList.isNotEmpty()) {
                fullAppList = appList
                appListAdapter = AppListAdapter(appList, this)
                with(binding.rvAppList) {
                    layoutManager = LinearLayoutManager(this@AppListActivity)
                    setHasFixedSize(true)
                    isNestedScrollingEnabled = false
                    adapter = appListAdapter
                }
                binding.pBar.visibility = View.GONE
                binding.rlAppList.visibility = View.VISIBLE
                binding.appBarLayout.visibility = View.VISIBLE
                binding.rlSearch.visibility = View.VISIBLE
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
