package com.example.downloadscheduler.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.downloadscheduler.db.database.TimerRoomDatabase
import com.example.downloadscheduler.db.entities.TimerTable
import com.example.downloadscheduler.utils.DownloadUrlFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimeService : Service() {
    // run on another Thread to avoid crash
    private val mHandler = Handler()

    // timer handling
    private var mTimer: Timer? = null
    val NOTIFY_INTERVAL = (5 * 1000).toLong() // 10 seconds

    var TAG="TimeService"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer!!.cancel()
        } else {
            // recreate new
            mTimer = Timer()
        }
        // schedule task
        mTimer!!.scheduleAtFixedRate(TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL)
    }

    internal inner class TimeDisplayTimerTask : TimerTask() {
        private// get date time in custom format
        val dateTime: String
            get() {
                val sdf = SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]")
                return sdf.format(Date())
            }

        override fun run() {
            // run on another thread
            mHandler.post { // display toast

                CoroutineScope(Dispatchers.IO).launch {
                    var alTimers:List<TimerTable> = TimerRoomDatabase.getInstance(applicationContext).timerDao().getAllTimersToProcess(getCurrentTime())
                    Log.e(TAG,"${alTimers.size}")
                    for(i in alTimers.indices){
                        Log.e(TAG,"Timer size ${alTimers.size}")

                        //not processed yet
                        if(!alTimers[i].isProcessing){
                            Log.e(TAG,"isProcessing${alTimers[i].isProcessing}")

                            TimerRoomDatabase.getInstance(applicationContext).timerDao().updateProcessingTask(alTimers[i].id,true)
                            Log.e(TAG,"updated processing")

                            processSingleTimer(alTimers[i])
                        }
                    }
                    /*Toast.makeText(
                        applicationContext, dateTime,
                        Toast.LENGTH_SHORT
                    ).show()*/
                }
            }
        }
    }

    fun getCurrentTime():String{
        var calendar=Calendar.getInstance()
        var currentYear = calendar.get(Calendar.YEAR)
        var currentMonth = calendar.get(Calendar.MONTH)+1
        var currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        var currentHour=calendar.get(Calendar.HOUR_OF_DAY)
        var currentMinute=calendar.get(Calendar.MINUTE)
        return "$currentDay/$currentMonth/$currentYear $currentHour:$currentMinute"
    }

    fun processSingleTimer(timer:TimerTable){
        var alUrl=ArrayList<String>()

        var url1=timer.url1
        var url2=timer.url2
        var url3=timer.url3
        var url4=timer.url4
        var url5=timer.url5

        if(url1.isNotEmpty()){alUrl.add(url1)}
        if(url2.isNotEmpty()){alUrl.add(url2)}
        if(url3.isNotEmpty()){alUrl.add(url3)}
        if(url4.isNotEmpty()){alUrl.add(url4)}
        if(url5.isNotEmpty()){alUrl.add(url5)}
        Log.e(TAG,"downloadAllUrlFiles")
        DownloadUrlFile.downloadAllUrlFiles(applicationContext,alUrl)

    }
}