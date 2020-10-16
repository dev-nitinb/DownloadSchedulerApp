package com.example.downloadscheduler.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.downloadscheduler.db.doa.TimerDao
import com.example.downloadscheduler.db.entities.TimerTable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [TimerTable::class], version = 1, exportSchema = false)
abstract class TimerRoomDatabase : RoomDatabase(){

    abstract fun timerDao():TimerDao

    private val NUMBER_OF_THREADS = 4
    var databaseWriteExecutor: ExecutorService =Executors.newFixedThreadPool(NUMBER_OF_THREADS)

    companion object{
        var instance:TimerRoomDatabase?=null
        private val DATABASE_NAME="timer_database"

        fun getInstance(context: Context):TimerRoomDatabase{
            if(instance==null){
                instance=Room
                    .databaseBuilder(context, TimerRoomDatabase::class.java, DATABASE_NAME)
                    .build()
            }
            return instance!!
        }
    }

}