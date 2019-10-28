@file:Suppress("DEPRECATION")

package com.biceps_studio.contact.helper

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast

class Utils {

    companion object {
        const val ID_CONTACT = "idContact"

        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun getLoading(context: Context) : ProgressDialog {
            val progressDialog = ProgressDialog(context)
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setMessage("Please wait...")

            return progressDialog
        }

        fun showLoading(progressDialog: ProgressDialog) {
            if (!progressDialog.isShowing) {
                progressDialog.show()
            }
        }

        fun hideLoading(progressDialog: ProgressDialog) {
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        }
    }
}