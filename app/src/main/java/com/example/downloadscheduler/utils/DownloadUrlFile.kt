package com.example.downloadscheduler.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.downloadscheduler.R
import com.example.downloadscheduler.api.RetrofitInstance
import com.example.downloadscheduler.ui.activity.ViewFilesActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URI

object DownloadUrlFile {
    val TAG="DownloadUrlFile"
    var retrofitService= RetrofitInstance.api
    var totalFilesDownloaded=0
    lateinit var mContext:Context
    var malUrl=ArrayList<String>()


    fun downloadAllUrlFiles(context: Context,alUrl:ArrayList<String>){

        mContext=context
        malUrl.clear()
        malUrl.addAll(alUrl)
        var totalNumberOfFiles=alUrl.size
        for (i in 0 until totalNumberOfFiles){
            var currentFileNumber= i+1
            downloadFileFromUrl(alUrl[i], currentFileNumber, totalNumberOfFiles)
        }
        totalFilesDownloaded=0
    }

    //download file using url
    private fun downloadFileFromUrl(fileUrlString: String, currentFileNumber: Int, totalNumberOfFiles: Int){
        var fileName= File(URI(fileUrlString).path).name
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
                        Log.e(TAG, "Success")

                        totalFilesDownloaded++

                        Log.e(TAG, "File download was a success? $writtenToDisk")
                        Log.e(TAG, "$totalFilesDownloaded of $totalNumberOfFiles downloaded")
                        playNotificationSound()
                        sendNotification("$totalFilesDownloaded / $totalNumberOfFiles files downloaded")

                        if (totalFilesDownloaded == totalNumberOfFiles) {
                            Log.e(TAG, "All files downloaded successfully!")

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
                Log.e(TAG, "error")
            }
        })
    }

    //write file in download folder
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

    //play sound after download
    fun playNotificationSound(){
        var notification= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var mediaPlayer= MediaPlayer.create(mContext, notification)
        mediaPlayer.start()
    }

    fun sendNotification(message: String) {
        //notification manager
        val mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        val mainIntent = Intent(mContext, ViewFilesActivity::class.java)
        Log.e(TAG, "$malUrl")
        mainIntent.putExtra("alUrl", malUrl)
        val pendingIntent = PendingIntent.getActivity(
            mContext,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

/*
        //custom notification view
        val notificationtView = RemoteViews(packageName, R.layout.custom_notification)
        notificationtView.setTextViewText(R.id.tvTitle, "Downloading Files")
        notificationtView.setTextViewText(R.id.tvInfo, "$message")
*/

        //notification builder
        val builder = NotificationCompat.Builder(mContext, idChannel)
        builder
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setContentTitle("Downloading Files")
            .setContentText("$message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(mContext, R.color.colorBlack))
            .setVibrate(longArrayOf(100, 250))
            .setLights(Color.YELLOW, 500, 5000)
            .setAutoCancel(false)

        mNotificationManager.notify(1, builder.build())

    }

}