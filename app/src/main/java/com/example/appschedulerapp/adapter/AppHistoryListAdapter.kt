package com.example.appschedulerapp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appschedulerapp.R
import com.example.appschedulerapp.handler.dbhandler.DatabaseHandler
import com.example.appschedulerapp.model.AppSelectionModel

class AppHistoryListAdapter(var apps: ArrayList<AppSelectionModel>, private var isDeleted: Deleted) : RecyclerView.Adapter<AppHistoryListAdapter.ParentViewHolder>() {

    interface Deleted {
        fun deleted(isDeleted:Boolean)
    }

    private lateinit var context: Context
    private var activity: Activity? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
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
        holder.ivIcon?.setImageDrawable(context.packageManager.getApplicationIcon(apps[position].appPackageName.toString()))
        holder.tvNameOfApp?.text = apps[position].appName
        holder.tvPackageNameOfApp?.text = apps[position].appPackageName
        holder.tvPackageSelectionDate?.text = apps[position].dateTime
        holder.tvPackageSelectionDate?.visibility = View.VISIBLE
        holder.llDelete?.visibility = View.VISIBLE
        if (!apps[position].note.isNullOrEmpty()) {
            holder.tvNote?.text = apps[position].note
            holder.tvNote?.visibility = View.VISIBLE
        }
        holder.ivDeleteOnly?.setOnClickListener {
           val status = DatabaseHandler(context).deleteHistory(apps[position].id)
            if (status != null) {
                if(status > -1) isDeleted.deleted(true)
            }
        }
    }

    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView? = itemView.findViewById<ImageView>(R.id.ivSelectedAppIcon)
        val tvNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvNameOfApp)
        val tvPackageNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvPackageNameOfApp)
        val tvPackageSelectionDate: TextView? = itemView.findViewById<TextView>(R.id.tvPackageSelectionDate)
        val tvNote: TextView? = itemView.findViewById<TextView>(R.id.tvNote)
        val ivDeleteOnly: ImageView? = itemView.findViewById<ImageView>(R.id.ivDeleteOnly)
        val llDelete: LinearLayout? = itemView.findViewById<LinearLayout>(R.id.llDelete)
    }
}
