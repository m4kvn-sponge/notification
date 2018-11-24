package com.m4kvn.loginnotification

import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import javax.inject.Inject

@Plugin(
    id = "login-notification",
    name = "Login Notification",
    version = "1.0-SNAPSHOT",
    description = "Notify that the player has logged in"
)
class Main {

    @Listener
    fun onServerStart(event: GameStartedServerEvent) {
        info("Sponge Server Plugin Login Notification on Start.")
    }

    @Listener
    fun onJoin(event: ClientConnectionEvent.Join) {
        info("onJoin=${event.targetEntity.displayNameData.displayName().get().toPlain()}")
    }

    @Listener
    fun onDisconnect(event: ClientConnectionEvent.Disconnect) {
        info("onDisconnect=${event.targetEntity.displayNameData.displayName().get().toPlain()}")
    }

    private fun info(message: String) {
        val text = Text.builder("[LoginNotification] $message")
            .color(TextColors.AQUA).build()
        Sponge.getGame().server.console.sendMessage(text)
    }
}