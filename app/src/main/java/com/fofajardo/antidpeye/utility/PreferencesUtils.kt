package com.fofajardo.antidpeye.utility

import android.content.Context
import com.fofajardo.antidpeye.data.Mode
import com.fofajardo.antidpeye.data.SettingsRepository

fun Context.getSettingsRepository(): SettingsRepository = SettingsRepository(this.applicationContext)

fun Context.currentMode(): Mode {
    val settings = getSettingsRepository().getSettingsBlocking()
    return Mode.fromString(settings.mode)
}
