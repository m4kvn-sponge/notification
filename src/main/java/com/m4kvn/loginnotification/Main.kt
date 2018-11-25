package com.m4kvn.loginnotification

import com.m4kvn.loginnotification.service.discord.DiscordService
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppedServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.file.Path
import javax.inject.Inject


@Plugin(
    id = "login-notification",
    name = "Login Notification",
    version = "1.0-SNAPSHOT",
    description = "Notify that the player has logged in"
)
class Main {

    @Inject
    lateinit var logger: Logger

    @Inject
    @ConfigDir(sharedRoot = false)
    lateinit var privateConfigDir: Path

    private val baseUrl = "https://discordapp.com/api/webhooks/"

    private val discord: DiscordService by lazy {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor
            .Logger { logger.debug(it) })
            .apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
        retrofit.create(DiscordService::class.java)
    }

    private val configLoader: HoconConfigurationLoader by lazy {
        HoconConfigurationLoader.builder()
            .setPath(privateConfigDir)
            .build()
    }

    private val configRoot: ConfigurationNode by lazy {
        if (configLoader.canLoad())
            configLoader.load() else
            configLoader.createEmptyNode()
    }

    private val discordUrl: String
        get() = configRoot.getNode("discord", "webhook").getString("")

    @Listener
    fun onGameLoadComplete(event: GameLoadCompleteEvent) {
        configRoot.getNode("discord", "webhook").apply {
            if (getString("").isNullOrBlank()) {
                value = ""
                info("Please add your discord webhook url to config file.")
            }
        }
        if (configLoader.canSave()) {
            configLoader.save(configRoot)
        }
    }

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        val message = "Server Started (${Sponge.getServer().motd.toPlain()})"
        info(message)
        discord.postChannelMessage(discordUrl, mapOf("content" to message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    @Listener
    fun onServerStop(event: GameStoppedServerEvent) {
        val message = "Server stopped (${Sponge.getServer().motd.toPlain()})"
        info(message)
        discord.postChannelMessage(discordUrl, mapOf("content" to message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    @Listener
    fun onJoin(event: ClientConnectionEvent.Join) {
        val message = "Player joined (${event.playerName})"
        info(message)
        discord.postChannelMessage(discordUrl, mapOf("content" to message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect) {
        val message = "Player disconnected (${event.playerName})"
        info(message)
        discord.postChannelMessage(discordUrl, mapOf("content" to message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    private fun info(message: String) {
        val text = Text.builder("[LoginNotification] $message")
            .color(TextColors.AQUA).build()
        Sponge.getGame().server.console.sendMessage(text)
    }

    private val TargetPlayerEvent.playerName: String
        get() = targetEntity.displayNameData.displayName().get().toPlain()
}