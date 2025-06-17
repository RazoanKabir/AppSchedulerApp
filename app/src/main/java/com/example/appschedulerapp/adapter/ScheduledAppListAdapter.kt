package com.example.appschedulerapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appschedulerapp.R
import com.example.appschedulerapp.model.AppSelectionModel
import com.example.appschedulerapp.util.Constants
import kotlinx.serialization.json.Json
import com.example.appschedulerapp.util.ViewDialog
import com.example.appschedulerapp.views.AddAppActivity

class ScheduledAppListAdapter(
    var apps: ArrayList<AppSelectionModel>,
    private val deletedApp: ViewDialog.DeletedApp
) : RecyclerView.Adapter<ScheduledAppListAdapter.ParentViewHolder>() {

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
        if (!apps[position].note.isNullOrEmpty()) {
            holder.tvNote?.text = apps[position].note
            holder.tvNote?.visibility = View.VISIBLE
        }
        holder.llEditAndDelete?.visibility = View.VISIBLE

        holder.ivEdit?.setOnClickListener {
            val appToJson = Json.encodeToString(AppSelectionModel.serializer(), apps[position])
            val intent = Intent(context, AddAppActivity::class.java)
            intent.putExtra(Constants.appToJSON, appToJson)
            intent.putExtra(Constants.from, Constants.edit)
            context.startActivity(intent)
        }

        holder.ivDelete?.setOnClickListener {
            val alert = ViewDialog()
            alert.showDeleteDialog(context, apps[position], deletedApp)
        }
    }

    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView? = itemView.findViewById<ImageView>(R.id.ivSelectedAppIcon)
        val ivEdit: ImageView? = itemView.findViewById<ImageView>(R.id.ivEdit)
        val ivDelete: ImageView? = itemView.findViewById<ImageView>(R.id.ivDelete)
        val tvNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvNameOfApp)
        val tvPackageNameOfApp: TextView? = itemView.findViewById<TextView>(R.id.tvPackageNameOfApp)
        val tvPackageSelectionDate: TextView? =
            itemView.findViewById<TextView>(R.id.tvPackageSelectionDate)
        val tvNote: TextView? = itemView.findViewById<TextView>(R.id.tvNote)
        val llEditAndDelete: LinearLayout? =
            itemView.findViewById<LinearLayout>(R.id.llEditAndDelete)
    }
}
