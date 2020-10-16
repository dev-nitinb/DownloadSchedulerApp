package com.example.downloadscheduler.ui.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.NestedScrollView
import com.example.downloadscheduler.R
import com.example.downloadscheduler.api.DownloadServiceApi
import com.example.downloadscheduler.api.RetrofitInstance
import com.example.downloadscheduler.db.database.TimerRoomDatabase
import com.example.downloadscheduler.db.entities.TimerTable
import com.example.downloadscheduler.utils.ProjectUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class AddTimerActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    var TAG= "AddTimerActivity"
    lateinit var llDynamicView: LinearLayoutCompat
    lateinit var btnAddUrl: AppCompatButton
    lateinit var btnAddTimer: AppCompatButton
    lateinit var nestedScrollView: NestedScrollView
    var totalUrlCounter = 0
    lateinit var alUrl: ArrayList<String>
    lateinit var mProjectUtils: ProjectUtils
    lateinit var retrofitService: DownloadServiceApi
    var totalFilesDownloaded=0
    lateinit var mTtimerDb: TimerRoomDatabase

    var selectedYear:Int=0
    var selectedMonth:Int=0
    var selectedDay:Int=0
    var selectedHour:Int=0
    var selectedMinute:Int=0
    var currentDate=""

    lateinit var calendar:Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timer)

        bindView()

    }

    private fun bindView() {
        llDynamicView = findViewById(R.id.llDynamicView)
        btnAddUrl = findViewById(R.id.btnAddUrl)
        btnAddTimer = findViewById(R.id.btnAddTimer)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        alUrl = ArrayList()

        supportActionBar!!.title = "Add Timer"
        mProjectUtils=ProjectUtils.getInstance(this@AddTimerActivity)
        retrofitService= RetrofitInstance.api
        mTtimerDb=TimerRoomDatabase.getInstance(this)

        calendar=Calendar.getInstance()

        setListener()
    }

    private fun setListener() {
        btnAddUrl.setOnClickListener {
            addUrlView()
        }

        btnAddTimer.setOnClickListener {
            hideKeyboard()

            if(dynamicLayoutValidation()){

               /* sendNotification("File Downloading Started")
                var totalNumberOfFiles=alUrl.size
                for (i in 0 until totalNumberOfFiles){
                    var currentFileNumber= i+1
                    downloadFileFromUrl(alUrl[i], currentFileNumber, totalNumberOfFiles)
                }
                totalFilesDownloaded=0*/

                var currentYear = calendar.get(Calendar.YEAR)
                var currentMonth = calendar.get(Calendar.MONTH)+1
                var currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                currentDate="$currentDay/$currentMonth/$currentYear"
                //mProjectUtils.showSnackbarMessage(nestedScrollView,"Date: $currentDate")

                var datePickerDialog=DatePickerDialog(this, this, currentYear, currentMonth-1, currentDay)
                datePickerDialog.datePicker.minDate=System.currentTimeMillis()-1000
                //max i month after current date
                //datePickerDialog.datePicker.maxDate=System.currentTimeMillis()
                datePickerDialog.show()
            }
        }
    }

    //add dynamic layout
    private fun addUrlView() {
        // add max 5 childs
        if (totalUrlCounter < 5) {
            totalUrlCounter++
            var addUrlView = layoutInflater.inflate(R.layout.add_row_url, null, false)

            var tiUrl = addUrlView.findViewById<TextInputLayout>(R.id.tiUrl)
            var etUrl = addUrlView.findViewById<TextInputEditText>(R.id.etUrl)
            var ivCancel = addUrlView.findViewById<AppCompatImageView>(R.id.ivCancel)

            tiUrl.hint = "Url $totalUrlCounter"
            ivCancel.setOnClickListener { removeView(addUrlView) }

            llDynamicView.addView(addUrlView)
            btnAddTimer.visibility = View.VISIBLE

            if (totalUrlCounter == 5) {
                btnAddUrl.visibility = View.GONE
            }
        } else {
            btnAddUrl.visibility = View.GONE
            mProjectUtils.showSnackbarMessage(nestedScrollView, "You have added max number of url!")
        }
    }

    //remove dynamic view
    private fun removeView(view: View) {
        if (totalUrlCounter == 1) {
            btnAddTimer.visibility = View.GONE
        } else if (totalUrlCounter == 5) {
            btnAddUrl.visibility = View.VISIBLE
        }
        totalUrlCounter--
        llDynamicView.removeView(view)
        //rename edit text hint
        for (i in 0 until llDynamicView.childCount) {
            val dynamicView: View = llDynamicView.getChildAt(i)
            var tiUrl = dynamicView.findViewById<TextInputLayout>(R.id.tiUrl)
            tiUrl.hint = "Url ${i + 1}"
        }
    }

    //validate dynamic edit text url
    private fun dynamicLayoutValidation(): Boolean {
        alUrl.clear()
        var isValidation = true
        for (i in 0 until llDynamicView.childCount) {
            val dynamicView: View = llDynamicView.getChildAt(i)
            var tiUrl = dynamicView.findViewById<TextInputLayout>(R.id.tiUrl)
            var etUrl = dynamicView.findViewById<TextInputEditText>(R.id.etUrl)
            var ivCancel = dynamicView.findViewById<AppCompatImageView>(R.id.ivCancel)

            if (etUrl.text.toString().isEmpty()) {
                etUrl.requestFocus()
                tiUrl.error = "Url Empty"
                isValidation = false
                break
            } else {
                alUrl.add(etUrl.text.toString())
            }
        }

        return isValidation
    }

    //hide keyboard
    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun insertTimerInDb(timerTable: TimerTable){
        CoroutineScope(Dispatchers.IO).launch {
            mTtimerDb.timerDao().insertTimer(timerTable)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedYear=year
        selectedMonth=month+1
        selectedDay=dayOfMonth

        var selectedDate="$selectedDay/$selectedMonth/$selectedYear"
        //mProjectUtils.showSnackbarMessage(nestedScrollView,"Date: $selectedDate")

        lateinit var timePickerDialog:TimePickerDialog
        timePickerDialog = if(selectedDate == currentDate){
            var currentHour=calendar.get(Calendar.HOUR_OF_DAY)
            var currentMinute=calendar.get(Calendar.MINUTE)
            TimePickerDialog(this,this,currentHour,currentMinute+3,true)
        } else{
            TimePickerDialog(this,this,0,0,true)
        }

        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedHour=hourOfDay
        selectedMinute=minute
       // mProjectUtils.showSnackbarMessage(nestedScrollView,"Date: $selectedHour/$selectedMinute")

        if (selectedYear == calendar.get(Calendar.YEAR)
            && selectedMonth == calendar.get(Calendar.MONTH)+1
            && selectedDay == calendar.get(Calendar.DAY_OF_MONTH)
            && (selectedHour < calendar.get(Calendar.HOUR_OF_DAY) || (selectedHour == calendar.get(Calendar.HOUR_OF_DAY) && selectedMinute <= (calendar.get(Calendar.MINUTE)+1)))
        ) {
            mProjectUtils.showSnackbarMessage(nestedScrollView,"Set time at least 2 minute from now")
        } else {
            var selectedDate="$selectedDay/$selectedMonth/$selectedYear $selectedHour:$selectedMinute"
            mProjectUtils.showSnackbarMessage(nestedScrollView,"Date: $selectedDate")
            var url1=""
            var url2=""
            var url3=""
            var url4=""
            var url5=""
            when (alUrl.size) {
                1 -> {
                    url1 = alUrl[0]
                }
                2 -> {
                    url1 = alUrl[0]
                    url2 = alUrl[1]
                }
                3 -> {
                    url1 = alUrl[0]
                    url2 = alUrl[1]
                    url3 = alUrl[2]
                }
                4 -> {
                    url1 = alUrl[0]
                    url2 = alUrl[1]
                    url3 = alUrl[2]
                    url4 = alUrl[3]
                }
                5 -> {
                    url1 = alUrl[0]
                    url2 = alUrl[1]
                    url3 = alUrl[2]
                    url4 = alUrl[3]
                    url5 = alUrl[4]
                }
            }
            var timerTable=TimerTable(selectedDate,url1, url2, url3, url4, url5)
            insertTimerInDb(timerTable)
            mProjectUtils.showSnackbarMessage(nestedScrollView,"Timer set successfully!")

            onBackPressed()
        }

    }
}