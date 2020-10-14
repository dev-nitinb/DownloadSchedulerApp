package com.example.downloadscheduler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadscheduler.R
import java.io.File
import java.net.URI

class RecyclerViewAdapter(var alUrl:ArrayList<String>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_files, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alUrl.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFileName.text = "${position+1}. ${File(URI(alUrl[position]).path).name}"
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName = itemView.findViewById(R.id.tvFileName) as TextView
    }

}