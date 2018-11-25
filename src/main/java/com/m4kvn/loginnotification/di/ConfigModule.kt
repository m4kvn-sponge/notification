package com.m4kvn.loginnotification.di

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.koin.core.KoinContext
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.Path

class ConfigModule(
    private val privateConfigDir: java.nio.file.Path
) : Module {

    override fun invoke(context: KoinContext): ModuleDefinition =
        module { invoke(this) }.invoke(context)

    operator fun invoke(moduleDefinition: ModuleDefinition) =
        moduleDefinition.module(Path.ROOT) {

            val configLoader: HoconConfigurationLoader =
                HoconConfigurationLoader.builder()
                    .setPath(privateConfigDir)
                    .build()

            single { configLoader }

            single<ConfigurationNode>(name = "root") {
                if (configLoader.canLoad())
                    configLoader.load() else
                    configLoader.createEmptyNode()
            }
        }
}