package com.example.downloadscheduler.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_table")
class TimerTable(

    @ColumnInfo(name = "date")
    var date:String,

    @ColumnInfo(name = "url1")
    var url1: String,

    @ColumnInfo(name = "url2")
    var url2: String,

    @ColumnInfo(name = "url3")
    var url3: String,

    @ColumnInfo(name = "url4")
    var url4: String,

    @ColumnInfo(name = "url5")
    var url5: String
){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int=0

    @ColumnInfo(name = "isProcessing")
    var isProcessing: Boolean=false

}

