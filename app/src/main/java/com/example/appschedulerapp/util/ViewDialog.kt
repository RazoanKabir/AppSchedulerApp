package com.example.appschedulerapp.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import com.example.appschedulerapp.R
import com.example.appschedulerapp.handler.dbhandler.DatabaseHandler
import com.example.appschedulerapp.model.AppSelectionModel

class ViewDialog {
    interface DeletedApp {
        fun appDeleted(isDeleted: Boolean)
    }

    fun showDeleteDialog(context: Context?,app: AppSelectionModel?, deletedApp: DeletedApp) {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_delete_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mDialogNo: FrameLayout? = dialog?.findViewById(R.id.frmNo)
        val mDialogOk: FrameLayout? = dialog?.findViewById(R.id.frmOk)
        val title: TextView? = dialog?.findViewById(R.id.tvDialogTitle)
        val subTitle: TextView? = dialog?.findViewById(R.id.tvDialogSubTitle)
        title?.text = context?.getString(R.string.delete_title)
        subTitle?.text = context?.getString(R.string.removeThisApp)
        mDialogNo?.setOnClickListener {
            dialog.dismiss()
        }
        mDialogOk?.setOnClickListener {
            context.let { it1 -> DatabaseHandler(it1).deleteSchedule(app?.id, context) }
            dialog.dismiss()
            deletedApp.appDeleted(true)
        }
        dialog?.show()
    }
}