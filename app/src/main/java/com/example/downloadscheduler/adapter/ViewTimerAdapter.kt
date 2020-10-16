package com.example.downloadscheduler.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadscheduler.R
import com.example.downloadscheduler.db.entities.TimerTable
import java.io.File
import java.net.URI

class ViewTimerAdapter(var alTimer:ArrayList<TimerTable>): RecyclerView.Adapter<ViewTimerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alTimer.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(alTimer[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTimerDate = itemView.findViewById(R.id.tvTimerDate) as AppCompatTextView
        val tvUrl1 = itemView.findViewById(R.id.tvUrl1) as AppCompatTextView
        val tvUrl2 = itemView.findViewById(R.id.tvUrl2) as AppCompatTextView
        val tvUrl3 = itemView.findViewById(R.id.tvUrl3) as AppCompatTextView
        val tvUrl4 = itemView.findViewById(R.id.tvUrl4) as AppCompatTextView
        val tvUrl5 = itemView.findViewById(R.id.tvUrl5) as AppCompatTextView

        fun bindView(timer:TimerTable){
            tvTimerDate.text="TImer Date: ${timer.date}"
            tvUrl1.text=timer.url1
            tvUrl2.text=timer.url2
            tvUrl3.text=timer.url3
            tvUrl4.text=timer.url4
            tvUrl5.text=timer.url5

            if (timer.url5.isEmpty()){
                tvUrl5.visibility=View.GONE
            }
            if (timer.url4.isEmpty()){
                tvUrl4.visibility=View.GONE
            }
            if (timer.url3.isEmpty()){
                tvUrl3.visibility=View.GONE
            }
            if (timer.url2.isEmpty()){
                tvUrl2.visibility=View.GONE
            }
            if (timer.url1.isEmpty()){
                tvUrl1.visibility=View.GONE
            }
        }

    }

}