package com.example.downloadscheduler.db.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.downloadscheduler.db.entities.TimerTable

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTimer(timer: TimerTable?)

    @Query("select * from word_table")
    suspend fun getAllTimers(): List<TimerTable>

    @Query("select * from word_table where date= :date")
    suspend fun getAllTimersToProcess(date: String): List<TimerTable>

    @Query("update word_table SET isProcessing= :isProcessing WHERE id= :id")
    suspend fun updateProcessingTask(id: Int, isProcessing: Boolean)

}
