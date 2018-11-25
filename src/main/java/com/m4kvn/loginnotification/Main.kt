package com.m4kvn.loginnotification

import com.m4kvn.loginnotification.di.ConfigModule
import com.m4kvn.loginnotification.di.ServiceModule
import com.m4kvn.loginnotification.service.discord.DiscordService
import com.m4kvn.loginnotification.service.discord.model.DiscordChnnelMessage
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.koin.core.Koin
import org.koin.log.EmptyLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppedServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.nio.file.Path
import javax.inject.Inject


@Plugin(
    id = "login-notification",
    name = "Login Notification",
    version = "1.0-SNAPSHOT",
    description = "Notify that the player has logged in"
)
open class Main : KoinComponent {

    @Inject
    @ConfigDir(sharedRoot = false)
    lateinit var privateConfigDir: Path

    private val discord: DiscordService by inject()
    private val configLoader: HoconConfigurationLoader by inject()
    private val configRoot: ConfigurationNode by inject(name = "root")

    private val discordUrl: String
        get() = configRoot.getNode("discord", "webhook").getString("")

    open fun modules() = listOf(
        ConfigModule(privateConfigDir),
        ServiceModule()
    )

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        Koin.logger = EmptyLogger()
        startKoin(modules())
        configRoot.getNode("discord", "webhook").apply {
            if (getString("").isNullOrBlank()) {
                value = ""
                info("Please add your discord webhook url to config file.")
            }
        }
        if (configLoader.canSave()) {
            configLoader.save(configRoot)
        }
        val message = "Server Started (${Sponge.getServer().motd.toPlain()})"
        info(message)
        discord.postChannelMessage(discordUrl, DiscordChnnelMessage(message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    @Listener
    fun onServerStop(event: GameStoppedServerEvent) {
        val message = "Server stopped (${Sponge.getServer().motd.toPlain()})"
        info(message)
        discord.postChannelMessage(discordUrl, DiscordChnnelMessage(message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    @Listener
    fun onJoin(event: ClientConnectionEvent.Join) {
        val message = "Player joined (${event.playerName})"
        info(message)
        discord.postChannelMessage(discordUrl, DiscordChnnelMessage(message))
            .subscribeOn(Schedulers.io())
            .subscribeBy(onError = {
                it.printStackTrace()
            })
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect) {
        val message = "Player disconnected (${event.playerName})"
        info(message)
        discord.postChannelMessage(discordUrl, DiscordChnnelMessage(message))
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