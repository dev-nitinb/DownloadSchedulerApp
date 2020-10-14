package com.example.downloadscheduler.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.downloadscheduler.R
import com.example.downloadscheduler.utils.ProjectUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    lateinit var clParent: ConstraintLayout
    lateinit var fabAddTimer:FloatingActionButton
    var PERMISSION_MULTILE_REQUEST=101
    lateinit var mProjectUtils: ProjectUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()

    }

    private fun bindView(){
        clParent=findViewById(R.id.clParent)
        fabAddTimer=findViewById(R.id.fabAddTimer)
        mProjectUtils= ProjectUtils.getInstance(this)
        checkPermission()
    }

    private fun setListener(){
        fabAddTimer.setOnClickListener{
            fabAddTimer.isClickable=false
            var intent=Intent(this, AddTimerActivity::class.java)
            startActivity(intent)
            fabAddTimer.isClickable=true
        }
    }

    private fun checkPermission(){
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)+
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                mProjectUtils.showSnackbarMessage(clParent,"Read write permission not granted! \nPlease grant permission to proceed further.")

                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), PERMISSION_MULTILE_REQUEST)
            }
        }
        else{
            setListener()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_MULTILE_REQUEST->
                if(grantResults.isNotEmpty()){
                    val readExternalFilePermission=grantResults[0]== PackageManager.PERMISSION_GRANTED
                    val writeExternalFilePermission=grantResults[1]== PackageManager.PERMISSION_GRANTED

                    if(readExternalFilePermission && writeExternalFilePermission){
                        setListener()
                    }
                    else{
                        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){

                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), PERMISSION_MULTILE_REQUEST)
                        }
                    }
                }
            else{
                    mProjectUtils.showSnackbarMessage(clParent,"Read write permission not granted! \nPlease grant permission to proceed further.")
                }
        }
    }

}