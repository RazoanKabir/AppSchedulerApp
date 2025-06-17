package com.example.appschedulerapp.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.appschedulerapp.R
import com.example.appschedulerapp.databinding.ActivityMainBinding
import com.example.appschedulerapp.util.UtilClass
import com.example.appschedulerapp.util.ViewDialog
import com.example.appschedulerapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), ViewDialog.DeletedApp {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainVM: MainViewModel
    private var toolbarMenu: Menu? = null
    private var menuOption: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initActionBar()
        initView()
        updateUI()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
    }

    private fun initView() {
        mainVM = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun updateUI() {
        mainVM.viewAppList(
            this,
            binding.rvAppList,
            binding.rlAppList,
            binding.rlEmptyView,
            this
        )
    }

    private fun initListener() {
        binding.fabNewApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AddAppActivity::class.java)
        }
        binding.srDashboard.setOnRefreshListener {
            updateUI()
            binding.srDashboard.isRefreshing = false
        }
    }

    private fun deleteAllHistory() {
        mainVM.deleteAllHistory(this)
    }

    private fun deleteAllSchedules() {
        mainVM.deleteAll(this)
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_scheduler_menu, menu)
        toolbarMenu = menu
        menuOption = menu.findItem(R.id.app_scheduler_menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteAll -> {
                deleteAllSchedules(); true
            }

            R.id.appHistory -> {
                UtilClass.goToNextActivity(this, AppHistoryActivity::class.java); true
            }

            R.id.appHistoryDeleteAll -> {
                deleteAllHistory(); true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun appDeleted(isDeleted: Boolean) {
        if (isDeleted) updateUI()
    }
}