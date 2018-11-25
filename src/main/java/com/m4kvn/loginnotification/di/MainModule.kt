package com.m4kvn.loginnotification.di

import com.m4kvn.loginnotification.Messenger
import org.koin.core.KoinContext
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.Path

class MainModule : Module {

    override fun invoke(context: KoinContext): ModuleDefinition =
        module { invoke(this) }.invoke(context)

    operator fun invoke(moduleDefinition: ModuleDefinition) =
        moduleDefinition.module(Path.ROOT) {
            single { Messenger() }
        }
}