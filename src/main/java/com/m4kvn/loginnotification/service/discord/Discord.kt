package com.m4kvn.loginnotification.service.discord

import com.m4kvn.loginnotification.Config
import com.m4kvn.loginnotification.Messenger
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class Discord : KoinComponent {
    private val messenger: Messenger by inject()
    private val config: Config by inject()

    fun initialize() {
        config.root.getNode("discord", "webhook").apply {
            if (getString("").isNullOrBlank()) {
                value = ""
                messenger.info("Please add your discord webhook url to config file.")
            }
            config.save()
        }
    }
}