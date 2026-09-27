package com.fofajardo.antidpeye.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.data.AppSettings
import com.fofajardo.antidpeye.data.AppStatus
import com.fofajardo.antidpeye.data.FAILED_BROADCAST
import com.fofajardo.antidpeye.data.Mode
import com.fofajardo.antidpeye.data.SENDER
import com.fofajardo.antidpeye.data.STARTED_BROADCAST
import com.fofajardo.antidpeye.data.STOPPED_BROADCAST
import com.fofajardo.antidpeye.data.Sender
import com.fofajardo.antidpeye.services.ServiceManager
import com.fofajardo.antidpeye.services.appStatus
import com.fofajardo.antidpeye.ui.screens.MainScreen
import com.fofajardo.antidpeye.ui.theme.ByeDpiTheme
import com.fofajardo.antidpeye.utility.getSettingsRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName

        fun applyAppTheme(theme: String) {
            when (theme) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private var statusText by mutableStateOf("")
    private var buttonText by mutableStateOf("")
    private var proxyAddress by mutableStateOf("")
    private var buttonEnabled by mutableStateOf(false)

    private val vpnRegister =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                ServiceManager.start(this, Mode.VPN)
            } else {
                Toast
                    .makeText(
                        this,
                        R.string.vpn_permission_denied,
                        Toast.LENGTH_SHORT,
                    ).show()
                updateStatus()
            }
        }

    private val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                Log.d(TAG, "Received intent: ${intent?.action}")

                if (intent == null) {
                    Log.w(TAG, "Received null intent")
                    return
                }

                val senderOrd = intent.getIntExtra(SENDER, -1)
                val sender = Sender.entries.getOrNull(senderOrd)
                if (sender == null) {
                    Log.w(TAG, "Received intent with unknown sender: $senderOrd")
                    return
                }

                when (val action = intent.action) {
                    STARTED_BROADCAST,
                    STOPPED_BROADCAST,
                    -> updateStatus()

                    FAILED_BROADCAST -> {
                        Toast
                            .makeText(
                                context,
                                getString(R.string.failed_to_start, sender.name),
                                Toast.LENGTH_SHORT,
                            ).show()
                        updateStatus()
                    }

                    else -> Log.w(TAG, "Unknown action: $action")
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = getSettingsRepository()

        setContent {
            val settings by repository.settingsFlow.collectAsState(initial = AppSettings())

            ByeDpiTheme(appTheme = settings.theme, amoledTheme = settings.amoledTheme) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            actions = {
                                IconButton(
                                    onClick = {
                                        val intent =
                                            Intent(this@MainActivity, SettingsActivity::class.java)
                                        startActivity(intent)
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = stringResource(R.string.title_settings),
                                    )
                                }
                            },
                            colors =
                                TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        )
                    },
                ) { innerPadding ->
                    MainScreen(
                        buttonText = buttonText,
                        statusText = statusText,
                        proxyAddress = proxyAddress,
                        buttonEnabled = buttonEnabled,
                        onButtonClick = {
                            val (status, _) = appStatus
                            when (status) {
                                AppStatus.Halted -> start()
                                AppStatus.Running -> stop()
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }

        val intentFilter =
            IntentFilter().apply {
                addAction(STARTED_BROADCAST)
                addAction(STOPPED_BROADCAST)
                addAction(FAILED_BROADCAST)
            }

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, intentFilter)
        }

        lifecycleScope.launch {
            val settings = repository.getSettings()
            applyAppTheme(settings.theme)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun start() {
        lifecycleScope.launch {
            val settings = getSettingsRepository().getSettings()
            val mode = Mode.fromString(settings.mode)
            when (mode) {
                Mode.VPN -> {
                    val intentPrepare = VpnService.prepare(this@MainActivity)
                    if (intentPrepare != null) {
                        vpnRegister.launch(intentPrepare)
                    } else {
                        ServiceManager.start(this@MainActivity, Mode.VPN)
                    }
                }

                Mode.Proxy -> ServiceManager.start(this@MainActivity, Mode.Proxy)
            }
        }
    }

    private fun stop() {
        ServiceManager.stop(this)
    }

    private fun updateStatus() {
        val (status, currentMode) = appStatus

        Log.i(TAG, "Updating status: $status, $currentMode")

        lifecycleScope.launch {
            val settings = getSettingsRepository().getSettings()
            val proxyIp = settings.engine.proxyIp
            val proxyPort = settings.engine.proxyPort
            proxyAddress = getString(R.string.proxy_address, proxyIp, proxyPort)

            when (status) {
                AppStatus.Halted -> {
                    when (Mode.fromString(settings.mode)) {
                        Mode.VPN -> {
                            statusText = getString(R.string.vpn_disconnected)
                            buttonText = getString(R.string.vpn_connect)
                        }

                        Mode.Proxy -> {
                            statusText = getString(R.string.proxy_down)
                            buttonText = getString(R.string.proxy_start)
                        }
                    }
                    buttonEnabled = true
                }

                AppStatus.Running -> {
                    when (currentMode) {
                        Mode.VPN -> {
                            statusText = getString(R.string.vpn_connected)
                            buttonText = getString(R.string.vpn_disconnect)
                        }

                        Mode.Proxy -> {
                            statusText = getString(R.string.proxy_up)
                            buttonText = getString(R.string.proxy_stop)
                        }
                    }
                    buttonEnabled = true
                }
            }
        }
    }
}
