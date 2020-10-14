package com.example.downloadscheduler.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface DownloadServiceApi {

    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String?): Call<ResponseBody>

}