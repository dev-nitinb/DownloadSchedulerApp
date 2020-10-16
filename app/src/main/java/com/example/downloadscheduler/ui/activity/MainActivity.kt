package com.example.downloadscheduler.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadscheduler.R
import com.example.downloadscheduler.adapter.ViewTimerAdapter
import com.example.downloadscheduler.db.database.TimerRoomDatabase
import com.example.downloadscheduler.db.entities.TimerTable
import com.example.downloadscheduler.service.TimeService
import com.example.downloadscheduler.utils.ProjectUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() { lateinit var clParent: ConstraintLayout
    lateinit var fabAddTimer:FloatingActionButton
    var PERMISSION_MULTILE_REQUEST=101
    lateinit var mProjectUtils: ProjectUtils
    lateinit var mTimerDb: TimerRoomDatabase
    lateinit var mAdapter:ViewTimerAdapter
    lateinit var rvTimer: RecyclerView
    lateinit var alTimer:ArrayList<TimerTable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()

    }

    private fun bindView(){
        clParent=findViewById(R.id.clParent)
        fabAddTimer=findViewById(R.id.fabAddTimer)
        rvTimer=findViewById(R.id.rvTimer)
        mProjectUtils= ProjectUtils.getInstance(this)
        mTimerDb= TimerRoomDatabase.getInstance(this)

        alTimer= ArrayList()
        mAdapter = ViewTimerAdapter(alTimer)
        val linearLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        rvTimer.layoutManager = linearLayoutManager
        rvTimer.adapter = mAdapter

    }

    override fun onResume() {
        super.onResume()
        //check permission granted or not
        checkPermission()

    }

    private fun setListener(){
        fabAddTimer.setOnClickListener{
            fabAddTimer.isClickable=false
            var intent=Intent(this, AddTimerActivity::class.java)
            startActivity(intent)
            fabAddTimer.isClickable=true
        }
        startService(Intent(this, TimeService::class.java))
    }

    private fun checkPermission(){
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)+
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                mProjectUtils.showSnackbarMessage(
                    clParent,
                    "Read write permission not granted! \nPlease grant permission to proceed further."
                )

                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), PERMISSION_MULTILE_REQUEST
                )
            }
        }
        else{
            setListener()
            getAllTimers()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_MULTILE_REQUEST ->
                if (grantResults.isNotEmpty()) {
                    val readExternalFilePermission =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeExternalFilePermission =
                        grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (readExternalFilePermission && writeExternalFilePermission) {
                        setListener()
                        getAllTimers()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), PERMISSION_MULTILE_REQUEST
                            )
                        }
                    }
                } else {
                    mProjectUtils.showSnackbarMessage(
                        clParent,
                        "Read write permission not granted! \nPlease grant permission to proceed further."
                    )
                }
        }
    }

    private fun getAllTimers(){
        CoroutineScope(Dispatchers.IO).launch {
            val listTimer=mTimerDb.timerDao().getAllTimers()
            alTimer.clear()
            alTimer.addAll(listTimer)
        }
        mAdapter.notifyDataSetChanged()
    }
}