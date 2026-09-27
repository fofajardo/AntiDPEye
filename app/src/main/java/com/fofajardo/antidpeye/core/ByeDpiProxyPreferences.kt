package com.fofajardo.antidpeye.core

import com.fofajardo.antidpeye.data.EngineSettings
import com.fofajardo.antidpeye.utility.shellSplit

sealed interface ByeDpiProxyPreferences {
    companion object {
        fun fromEngineSettings(settings: EngineSettings): ByeDpiProxyPreferences =
            if (settings.enableCmdSettings) {
                ByeDpiProxyCmdPreferences(settings.cmdArgs)
            } else {
                ByeDpiProxyUIPreferences(settings)
            }
    }
}

class ByeDpiProxyCmdPreferences(
    val args: Array<String>,
) : ByeDpiProxyPreferences {
    constructor(cmd: String) : this(cmdToArgs(cmd))

    companion object {
        private fun cmdToArgs(cmd: String): Array<String> {
            val firstArgIndex = cmd.indexOf("-")
            val argsStr = (if (firstArgIndex > 0) cmd.substring(firstArgIndex) else cmd).trim()
            return arrayOf("ciadpi") + shellSplit(argsStr)
        }
    }
}

class ByeDpiProxyUIPreferences(
    val ip: String = "127.0.0.1",
    val port: Int = 1080,
    val maxConnections: Int = 512,
    val bufferSize: Int = 16384,
    val defaultTtl: Int = 0,
    val customTtl: Boolean = false,
    val noDomain: Boolean = false,
    val desyncHttp: Boolean = true,
    val desyncHttps: Boolean = true,
    val desyncUdp: Boolean = false,
    val desyncMethod: DesyncMethod = DesyncMethod.Disorder,
    val splitPosition: Int = 1,
    val splitAtHost: Boolean = false,
    val fakeTtl: Int = 8,
    val fakeSni: String = "www.iana.org",
    val oobChar: Byte = 'a'.code.toByte(),
    val hostMixedCase: Boolean = false,
    val domainMixedCase: Boolean = false,
    val hostRemoveSpaces: Boolean = false,
    val tlsRecordSplit: Boolean = false,
    val tlsRecordSplitPosition: Int = 0,
    val tlsRecordSplitAtSni: Boolean = false,
    val hostsMode: HostsMode = HostsMode.Disable,
    val hosts: String? = null,
    val tcpFastOpen: Boolean = false,
    val udpFakeCount: Int = 0,
    val dropSack: Boolean = false,
    val fakeOffset: Int = 0,
    val noIpv6: Boolean = false,
    val connIp: String? = null,
    val waitSend: Boolean = false,
    val awaitInt: Int = 0,
    val md5sig: Boolean = false,
    val fakeData: String? = null,
    val fakeTlsMod: String? = null,
    val tlsminor: Int = -1,
    val round: String? = null,
    val pf: String? = null,
    val ipset: String? = null,
    val auto: String? = null,
    val autoMode: String? = null,
    val timeout: String? = null,
) : ByeDpiProxyPreferences {
    constructor(s: EngineSettings) : this(
        ip = s.proxyIp.ifBlank { "127.0.0.1" },
        port = s.proxyPort.toIntOrNull() ?: 1080,
        maxConnections = s.maxConnections.toIntOrNull() ?: 512,
        bufferSize = s.bufferSize.toIntOrNull() ?: 16384,
        defaultTtl = s.defaultTtl.toIntOrNull() ?: 0,
        customTtl = s.defaultTtl.toIntOrNull() != null && (s.defaultTtl.toIntOrNull() ?: 0) > 0,
        noDomain = s.noDomain,
        desyncHttp = s.desyncHttp,
        desyncHttps = s.desyncHttps,
        desyncUdp = s.desyncUdp,
        desyncMethod = DesyncMethod.fromName(s.desyncMethod.ifBlank { "none" }),
        splitPosition = s.splitPosition.toIntOrNull() ?: 1,
        splitAtHost = s.splitAtHost,
        fakeTtl = s.fakeTtl.toIntOrNull() ?: 8,
        fakeSni = s.fakeSni.ifBlank { "www.iana.org" },
        oobChar = (s.oobData.ifBlank { "a" })[0].code.toByte(),
        hostMixedCase = s.hostMixedCase,
        domainMixedCase = s.domainMixedCase,
        hostRemoveSpaces = s.hostRemoveSpaces,
        tlsRecordSplit = s.tlsrecEnabled,
        tlsRecordSplitPosition = s.tlsrecPosition.toIntOrNull() ?: 0,
        tlsRecordSplitAtSni = s.tlsrecAtSni,
        hostsMode = HostsMode.fromName(s.hostsMode.ifBlank { "disable" }),
        hosts =
            when (s.hostsMode) {
                "blacklist" -> s.hostsBlacklist.trim().ifBlank { null }
                "whitelist" -> s.hostsWhitelist.trim().ifBlank { null }
                else -> null
            },
        tcpFastOpen = s.tcpFastOpen,
        udpFakeCount = s.udpFakeCount.toIntOrNull() ?: 0,
        dropSack = s.dropSack,
        fakeOffset = s.fakeOffset.toIntOrNull() ?: 0,
        noIpv6 = s.noIpv6,
        connIp = s.connIp.trim().ifBlank { null },
        waitSend = s.waitSend,
        awaitInt = s.awaitInt.toIntOrNull() ?: 0,
        md5sig = s.md5sig,
        fakeData = s.fakeData.trim().ifBlank { null },
        fakeTlsMod = s.fakeTlsMod.trim().ifBlank { null },
        tlsminor = s.tlsminor.toIntOrNull() ?: -1,
        round = s.round.trim().ifBlank { null },
        pf = s.pf.trim().ifBlank { null },
        ipset = s.ipset.trim().ifBlank { null },
        auto = s.auto.trim().ifBlank { null },
        autoMode = s.autoMode.trim().ifBlank { null },
        timeout = s.timeout.trim().ifBlank { null },
    )

    enum class DesyncMethod {
        None,
        Split,
        Disorder,
        Fake,
        OOB,
        DISOOB,
        ;

        companion object {
            fun fromName(name: String): DesyncMethod =
                when (name.lowercase()) {
                    "none" -> None
                    "split" -> Split
                    "disorder" -> Disorder
                    "fake" -> Fake
                    "oob" -> OOB
                    "disoob" -> DISOOB
                    else -> Disorder
                }
        }
    }

    enum class HostsMode {
        Disable,
        Blacklist,
        Whitelist,
        ;

        companion object {
            fun fromName(name: String): HostsMode =
                when (name.lowercase()) {
                    "disable", "none" -> Disable
                    "blacklist" -> Blacklist
                    "whitelist" -> Whitelist
                    else -> Disable
                }
        }
    }
}
