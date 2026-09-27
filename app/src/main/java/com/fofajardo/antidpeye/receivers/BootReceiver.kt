package com.fofajardo.antidpeye.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.fofajardo.antidpeye.data.Mode
import com.fofajardo.antidpeye.services.ServiceManager
import com.fofajardo.antidpeye.utility.getSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val action = intent.action
        if (Intent.ACTION_BOOT_COMPLETED != action &&
            Intent.ACTION_REBOOT != action &&
            "android.intent.action.QUICKBOOT_POWERON" != action
        ) {
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = context.getSettingsRepository()
                val settings = repository.getSettings()

                if (!settings.autostart) {
                    return@launch
                }

                val mode = Mode.fromString(settings.mode)
                if (mode == Mode.VPN && VpnService.prepare(context) != null) {
                    return@launch
                }

                ServiceManager.start(context, mode)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
