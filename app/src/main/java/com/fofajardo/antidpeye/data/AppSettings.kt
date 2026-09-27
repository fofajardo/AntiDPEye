package com.fofajardo.antidpeye.data

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val theme: String = "system",
    val amoledTheme: Boolean = false,
    val mode: String = "vpn",
    val autostart: Boolean = false,
    val dnsIp: String = "1.1.1.1",
    val ipv6Enable: Boolean = false,
    val vpnFilterMode: String = "blacklist",
    val vpnFilteredApps: Set<String> = emptySet(),
    val wasRunning: Boolean = false,
    val engine: EngineSettings = EngineSettings(),
)

@Serializable
data class EngineSettings(
    val enableCmdSettings: Boolean = false,
    val cmdArgs: String = "",
    // Proxy & Connection
    val proxyIp: String = "127.0.0.1",
    val proxyPort: String = "1080",
    val maxConnections: String = "512",
    val bufferSize: String = "16384",
    val connIp: String = "",
    val noIpv6: Boolean = false,
    val tcpFastOpen: Boolean = false,
    val noDomain: Boolean = false,
    val waitSend: Boolean = false,
    val awaitInt: String = "",
    // Desync Tactics
    val defaultTtl: String = "0",
    val desyncMethod: String = "none",
    val splitPosition: String = "0",
    val splitAtHost: Boolean = false,
    val dropSack: Boolean = false,
    val fakeTtl: String = "0",
    val fakeOffset: String = "0",
    val fakeSni: String = "",
    val md5sig: Boolean = false,
    val fakeData: String = "",
    val fakeTlsMod: String = "",
    val tlsminor: String = "",
    val round: String = "",
    val oobData: String = "",
    // Protocols
    val desyncHttp: Boolean = true,
    val desyncHttps: Boolean = true,
    val desyncUdp: Boolean = false,
    val hostMixedCase: Boolean = false,
    val domainMixedCase: Boolean = false,
    val hostRemoveSpaces: Boolean = false,
    val tlsrecEnabled: Boolean = false,
    val tlsrecPosition: String = "0",
    val tlsrecAtSni: Boolean = false,
    val udpFakeCount: String = "0",
    // Filters
    val hostsMode: String = "none",
    val hostsBlacklist: String = "",
    val hostsWhitelist: String = "",
    val pf: String = "",
    val ipset: String = "",
    // Auto
    val auto: String = "",
    val autoMode: String = "none",
    val timeout: String = "",
)
