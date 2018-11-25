package com.m4kvn.loginnotification.service.discord

import com.m4kvn.loginnotification.service.discord.model.DiscordChnnelMessage
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
        @Body data: DiscordChnnelMessage
    ): Completable
}