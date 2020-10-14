package com.example.downloadscheduler.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadscheduler.R
import com.example.downloadscheduler.adapter.RecyclerViewAdapter

class ViewFilesActivity : AppCompatActivity() {

    var TAG="ViewFilesActivity"
    lateinit var rvFiles:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_files)

        supportActionBar!!.title="View Files"
        var alUrl:ArrayList<String> = intent.getSerializableExtra("alUrl") as ArrayList<String>

        rvFiles=findViewById(R.id.rvFiles)
        Log.e(TAG,"$alUrl")

       var mAdapter = RecyclerViewAdapter( alUrl)
        val linearLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        rvFiles.layoutManager = linearLayoutManager
        rvFiles.adapter = mAdapter

    }

}