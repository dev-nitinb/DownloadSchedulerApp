package com.example.downloadscheduler.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.downloadscheduler.R
import com.example.downloadscheduler.api.DownloadServiceApi
import com.example.downloadscheduler.api.RetrofitInstance
import com.example.downloadscheduler.utils.ProjectUtils
import com.example.downloadscheduler.utils.PublicDownloadStorageDir
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URI


class AddTimerActivity : AppCompatActivity() {

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

        setListener()
    }

    private fun setListener() {
        btnAddUrl.setOnClickListener {
            addUrlView()
        }

        btnAddTimer.setOnClickListener {
            hideKeyboard()

            if(dynamicLayoutValidation()){
                sendNotification("File Downloading Started")
                var totalNumberOfFiles=alUrl.size
                for (i in 0 until totalNumberOfFiles){
                    var currentFileNumber= i+1
                    downloadFileFromUrl(alUrl[i], currentFileNumber, totalNumberOfFiles)
                }
                totalFilesDownloaded=0
            }
        }
    }

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

    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, fileName: String): Boolean {
        return try {
            val futureStudioIconFile = File(
                PublicDownloadStorageDir.getPublicDownloadStorageDir(),
                fileName
            )
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    //Log.e(TAG, "File download: $fileSizeDownloaded of $fileSize")
                }
                outputStream!!.flush()
                true
            } catch (e: IOException) {
                Log.e(TAG, e.message)
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
        catch (e: IOException) {
            Log.e(TAG, e.message)
            false
        }
    }

    private fun downloadFileFromUrl(
        fileUrlString: String,
        currentFileNumber: Int,
        totalNumberOfFiles: Int
    ){
        var fileName=File(URI(fileUrlString).path).name
        Log.e(TAG, "Start Download File name: $fileName")
        var call: Call<ResponseBody> = retrofitService.downloadFileWithDynamicUrlSync(fileUrlString)
        Log.e(TAG, "Call api")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>, response: Response<ResponseBody>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    Log.e(TAG, "Server contacted and has file $fileName")

                    val writtenToDisk: Boolean = writeResponseBodyToDisk(
                        response.body()!!,
                        fileName
                    )
                    if (writtenToDisk) {
                        mProjectUtils.showSnackbarMessage(nestedScrollView, "Success")
                        totalFilesDownloaded++

                        mProjectUtils.showSnackbarMessage(
                            nestedScrollView,
                            "File $currentFileNumber downloaded"
                        )
                        Log.e(TAG, "File download was a success? $writtenToDisk")
                        Log.e(TAG, "$totalFilesDownloaded of $totalNumberOfFiles downloaded")
                        playNotificationSound()
                        sendNotification("$totalFilesDownloaded / $totalNumberOfFiles files downloaded")

                        if (totalFilesDownloaded == totalNumberOfFiles) {
                            mProjectUtils.showSnackbarMessage(
                                nestedScrollView,
                                "All files downloaded successfully!"
                            )
                            sendNotification("All files downloaded successfully!")
                        }
                    } else {
                        Log.e(TAG, "Failed to download  $currentFileNumber")
                    }

                } else {
                    Log.d(TAG, "server contact failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                mProjectUtils.showSnackbarMessage(nestedScrollView, "Failed")
                Log.e(TAG, "error")
            }
        })
    }

    fun playNotificationSound(){
        var notification=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var mediaPlayer= MediaPlayer.create(applicationContext, notification)
        mediaPlayer.start()
    }

    fun sendNotification(message: String) {
        //notification manager
        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //create channel
        val idChannel = "channel_01"
        var mChannel: NotificationChannel? = null
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(idChannel, "Download Update", importance)
            // Configure the notification channel.
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mNotificationManager.createNotificationChannel(mChannel)
        }

        //intent
        val mainIntent = Intent(this, ViewFilesActivity::class.java)
        Log.e(TAG,"$alUrl")
        mainIntent.putExtra("alUrl",alUrl)
        val pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

/*
        //custom notification view
        val notificationtView = RemoteViews(packageName, R.layout.custom_notification)
        notificationtView.setTextViewText(R.id.tvTitle, "Downloading Files")
        notificationtView.setTextViewText(R.id.tvInfo, "$message")
*/

        //notification builder
        val builder = NotificationCompat.Builder(this, idChannel)
        builder
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setContentTitle("Downloading Files")
            .setContentText("$message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(this, R.color.colorBlack))
            .setVibrate(longArrayOf(100, 250))
            .setLights(Color.YELLOW, 500, 5000)
            .setAutoCancel(false)

        mNotificationManager.notify(1, builder.build())

    }
}