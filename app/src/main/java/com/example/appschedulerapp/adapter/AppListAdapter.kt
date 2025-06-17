package com.example.appschedulerapp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appschedulerapp.R
import com.example.appschedulerapp.model.PackageAppInfo

class AppListAdapter(var apps: ArrayList<PackageAppInfo>, private val selectedApp: SelectedApp) :
    RecyclerView.Adapter<AppListAdapter.ParentViewHolder>() {

    private lateinit var context: Context
    var activity: Activity? = null

    interface SelectedApp {
        fun appSelected(selectedApp: PackageAppInfo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParentViewHolder {
        context = parent.context
        activity = parent.context as Activity?

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_app_list, parent, false)
        return ParentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        holder.ivIcon?.setImageDrawable(apps[position].icon)
        holder.tvPackageNameOfApp?.text = apps[position].packageName
        holder.tvNameOfApp?.text = apps[position].appName

        holder.rlAppItem?.setOnClickListener {
            selectedApp.appSelected(apps[position])
        }
    }

    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView? = itemView.findViewById<ImageView>(R.id.ivSelectedAppIcon)
        val tvNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvNameOfApp)
        val tvPackageNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvPackageNameOfApp)
        val rlAppItem: RelativeLayout? = itemView.findViewById<RelativeLayout>(R.id.rlAppItem)
    }
}

