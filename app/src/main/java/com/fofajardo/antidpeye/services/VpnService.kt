package com.fofajardo.antidpeye.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.activities.MainActivity
import com.fofajardo.antidpeye.core.ByeDpiProxy
import com.fofajardo.antidpeye.core.ByeDpiProxyPreferences
import com.fofajardo.antidpeye.data.*
import dev.zeptun.Zeptun
import com.fofajardo.antidpeye.utility.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class VpnService : LifecycleVpnService() {
    private val byeDpiProxy = ByeDpiProxy()
    private var proxyJob: Job? = null
    private var tunFd: ParcelFileDescriptor? = null
    private val mutex = Mutex()
    private var stopping: Boolean = false

    companion object {
        private val TAG: String = VpnService::class.java.simpleName
        private const val FOREGROUND_SERVICE_ID: Int = 1
        private const val NOTIFICATION_CHANNEL_ID: String = "AntiDPEyeVpn"

        private var status: ServiceStatus = ServiceStatus.Disconnected
    }

    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.vpn_channel_name,
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return when (val action = intent?.action) {
            START_ACTION -> {
                lifecycleScope.launch { start() }
                START_STICKY
            }

            STOP_ACTION -> {
                lifecycleScope.launch { stop() }
                START_NOT_STICKY
            }

            else -> {
                Log.w(TAG, "Unknown action: $action")
                START_NOT_STICKY
            }
        }
    }

    override fun onRevoke() {
        Log.i(TAG, "VPN revoked")
        lifecycleScope.launch { stop() }
    }

    private suspend fun start() {
        Log.i(TAG, "Starting")

        if (status == ServiceStatus.Connected) {
            Log.w(TAG, "VPN already connected")
            return
        }

        try {
            val settings = getSettingsRepository().getSettings()
            mutex.withLock {
                startProxy(settings)
                startTun2Socks(settings)
            }
            updateStatus(ServiceStatus.Connected)
            startForeground()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
            updateStatus(ServiceStatus.Failed)
            stop()
        }
    }

    private fun startForeground() {
        val notification: Notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                FOREGROUND_SERVICE_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(FOREGROUND_SERVICE_ID, notification)
        }
    }

    private suspend fun stop() {
        Log.i(TAG, "Stopping")

        mutex.withLock {
            stopping = true
            try {
                stopTun2Socks()
                stopProxy()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop VPN", e)
            } finally {
                stopping = false
            }
        }

        updateStatus(ServiceStatus.Disconnected)
        stopSelf()
    }

    private suspend fun startProxy(settings: AppSettings) {
        Log.i(TAG, "Starting proxy")

        if (proxyJob != null) {
            Log.w(TAG, "Proxy fields not null")
            throw IllegalStateException("Proxy fields not null")
        }

        val preferences = ByeDpiProxyPreferences.fromEngineSettings(settings.engine)

        proxyJob = lifecycleScope.launch(Dispatchers.IO) {
            val code = byeDpiProxy.startProxy(preferences)

            withContext(Dispatchers.Main) {
                if (code != 0) {
                    Log.e(TAG, "Proxy stopped with code $code")
                    updateStatus(ServiceStatus.Failed)
                } else {
                    if (!stopping) {
                        stop()
                        updateStatus(ServiceStatus.Disconnected)
                    }
                }
            }
        }

        Log.i(TAG, "Proxy started")
    }

    private suspend fun stopProxy() {
        Log.i(TAG, "Stopping proxy")

        if (status == ServiceStatus.Disconnected) {
            Log.w(TAG, "Proxy already disconnected")
            return
        }

        byeDpiProxy.stopProxy()
        proxyJob?.join() ?: throw IllegalStateException("ProxyJob field null")
        proxyJob = null

        Log.i(TAG, "Proxy stopped")
    }

    private fun startTun2Socks(settings: AppSettings) {
        try {
            val ver = Zeptun.nativeVersion()
            Log.i(TAG, "Starting tun2socks (zeptun version: $ver)")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed reading zeptun version", e)
        }

        if (tunFd != null) {
            throw IllegalStateException("VPN field not null")
        }

        val port = settings.engine.proxyPort.toIntOrNull() ?: 1080
        val dns = settings.dnsIp

        val fd = createBuilder(settings).establish()
            ?: throw IllegalStateException("VPN connection failed")

        this.tunFd = fd

        val config = """
            preset = "mobile"

            [tun]
            fd = ${fd.fd}
            mtu = 8500

            [handler]
            kind = "socks5"

            [handler.socks5]
            server = "127.0.0.1:$port"
            pipeline = false
            optimistic_data = false

            [dns]
            hijack = true
            upstream = "$dns:53"
        """.trimIndent()

        // XXX: Although the fd is processed in the JNI code, the device kind
        //      is never updated by Zeptun. We workaround this by setting the
        //      fd again in the TOML config above. This could be removed once
        //      upstream either provides a proper fix or considers this as
        //      intended behavior.
        val rc = Zeptun.nativeStart(this, fd.fd, config)
        if (rc != 0) {
            Log.e(TAG, "Zeptun failed to start with rc: $rc")
            throw IllegalStateException("Zeptun start failed: $rc")
        }

        Log.i(TAG, "Zeptun started")
    }

    private fun stopTun2Socks() {
        Log.i(TAG, "Stopping tun2socks")

        Zeptun.nativeStop()

        tunFd?.close() ?: Log.w(TAG, "VPN not running")
        tunFd = null

        Log.i(TAG, "Tun2socks stopped")
    }

    private fun updateStatus(newStatus: ServiceStatus) {
        Log.d(TAG, "VPN status changed from $status to $newStatus")

        status = newStatus

        setStatus(
            when (newStatus) {
                ServiceStatus.Connected -> AppStatus.Running

                ServiceStatus.Disconnected,
                ServiceStatus.Failed -> {
                    proxyJob = null
                    AppStatus.Halted
                }
            },
            Mode.VPN
        )

        val intent = Intent(
            when (newStatus) {
                ServiceStatus.Connected -> STARTED_BROADCAST
                ServiceStatus.Disconnected -> STOPPED_BROADCAST
                ServiceStatus.Failed -> FAILED_BROADCAST
            }
        )
        intent.putExtra(SENDER, Sender.VPN.ordinal)
        sendBroadcast(intent)
    }

    private fun createNotification(): Notification =
        createConnectionNotification(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_title,
            R.string.vpn_notification_content,
            VpnService::class.java,
        )

    private fun createBuilder(settings: AppSettings): Builder {
        val dns = settings.dnsIp
        val ipv6 = settings.ipv6Enable
        Log.d(TAG, "DNS: $dns")
        val builder = Builder()
        builder.setSession("AntiDPEye")
        builder.setMtu(8500)
        builder.setConfigureIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )
        )

        builder.addAddress("172.19.0.1", 30)
            .addRoute("0.0.0.0", 0)

        if (ipv6) {
            builder.addAddress("fdfe:dcba:9876::1", 126)
                .addRoute("::", 0)
        }

        builder.addDnsServer("172.19.0.2")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false)
        }

        val filter = settings.vpnFilteredApps
        when (val filterMode = settings.vpnFilterMode) {
            "blacklist" -> {
                filter.forEach {
                    try {
                        builder.addDisallowedApplication(it)
                    } catch (ignore: PackageManager.NameNotFoundException) {}
                }
                builder.addDisallowedApplication(applicationContext.packageName)
            }

            "whitelist" -> {
                filter.forEach {
                    try {
                        builder.addAllowedApplication(it)
                    } catch (ignore: PackageManager.NameNotFoundException) {}
                }
            }

            else -> {
                Log.w(TAG, "Invalid VPN filter mode: $filterMode")
            }
        }

        return builder
    }
}
