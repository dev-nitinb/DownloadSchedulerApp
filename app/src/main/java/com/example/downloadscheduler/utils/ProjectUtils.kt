package com.example.downloadscheduler.utils

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

class ProjectUtils(mContext: Context) {

    fun showSnackbarMessage(view:View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    companion object{
        var mProjectUtils:ProjectUtils?=null
        fun getInstance(context: Context): ProjectUtils{
            if (mProjectUtils==null)
                mProjectUtils=ProjectUtils(context)

            return mProjectUtils!!
        }
    }

}