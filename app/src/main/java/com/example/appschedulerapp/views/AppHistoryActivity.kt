package com.example.appschedulerapp.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appschedulerapp.adapter.AppHistoryListAdapter
import com.example.appschedulerapp.databinding.ActivityAppHistoryBinding
import com.example.appschedulerapp.handler.dbhandler.DatabaseHandler
import com.example.appschedulerapp.model.AppSelectionModel

class AppHistoryActivity : AppCompatActivity(), AppHistoryListAdapter.Deleted {

    private lateinit var binding: ActivityAppHistoryBinding
    private var appHistoryListAdapter: AppHistoryListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initListener()
    }

    private fun initListener() {
        binding.srDashboard.setOnRefreshListener {
            initView()
            binding.srDashboard.isRefreshing = false
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initView() {
        val historyList: ArrayList<AppSelectionModel> =
            DatabaseHandler(this).viewScheduledAppHistory()

        if (historyList.isNotEmpty()) {
            binding.rvAppHistoryList.apply {
                layoutManager = LinearLayoutManager(
                    this@AppHistoryActivity,
                    LinearLayoutManager.VERTICAL,
                    true
                )
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                adapter = AppHistoryListAdapter(historyList, this@AppHistoryActivity)
                visibility = View.VISIBLE
            }
            binding.rlEmptyView.visibility = View.GONE
        } else {
            binding.rvAppHistoryList.visibility = View.GONE
            binding.rlEmptyView.visibility = View.VISIBLE
        }
    }

    override fun deleted(isDeleted: Boolean) {
        if (isDeleted) initView()
    }
}
