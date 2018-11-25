package com.m4kvn.loginnotification.service.discord

import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface DiscordService {

    @Headers(
        "Accept: application/json",
        "Content-type: application/json"
    )
    @POST
    fun postChannelMessage(
        @Url url: String,
        @Body data: Map<String, String>
    ): Completable
}