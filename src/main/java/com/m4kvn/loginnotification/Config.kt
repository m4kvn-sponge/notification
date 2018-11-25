package com.m4kvn.loginnotification

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class Config : KoinComponent {
    private val messenger: Messenger by inject()
    private val configLoader: HoconConfigurationLoader by inject()

    val root: ConfigurationNode by inject(name = "root")

    fun initialize() {
        initDiscordConfig()
    }

    fun terminate() {
        save()
    }

    fun save() {
        if (configLoader.canSave()) {
            configLoader.save(root)
        }
    }

    private fun initDiscordConfig() {
        root.getNode("discord", "webhook").apply {
            if (getString("").isNullOrBlank()) {
                value = ""
                messenger.info("Please add your discord webhook url to config file.")
            }
            save()
        }
    }
}