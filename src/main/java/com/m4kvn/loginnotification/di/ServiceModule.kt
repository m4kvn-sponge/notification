package com.m4kvn.loginnotification.di

import com.m4kvn.loginnotification.service.discord.DiscordService
import okhttp3.OkHttpClient
import org.koin.core.KoinContext
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.Path
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceModule : Module {

    override fun invoke(context: KoinContext): ModuleDefinition =
        module { invoke(this) }.invoke(context)

    operator fun invoke(moduleDefinition: ModuleDefinition) =
        moduleDefinition.module(Path.ROOT) {
            single {
                Retrofit.Builder()
                    .client(OkHttpClient.Builder().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl("https://discordapp.com/api/webhooks/")
                    .build()
                    .create(DiscordService::class.java)
            }
        }
}