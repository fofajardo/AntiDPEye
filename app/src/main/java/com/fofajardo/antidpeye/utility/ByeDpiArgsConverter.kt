package com.fofajardo.antidpeye.utility

import com.fofajardo.antidpeye.data.EngineSettings

object ByeDpiArgsConverter {
    fun engineSettingsToCmdArgs(settings: EngineSettings): String {
        val args = mutableListOf<String>()

        val ip = settings.proxyIp
        if (ip.isNotBlank() && ip != "127.0.0.1") {
            args.add("-i")
            args.add(ip)
        }

        val port = settings.proxyPort
        if (port.isNotBlank() && port != "1080") {
            args.add("-p")
            args.add(port)
        }

        val maxConn = settings.maxConnections
        if (maxConn.isNotBlank() && maxConn != "512" && (maxConn.toIntOrNull() ?: 0) > 0) {
            args.add("-c")
            args.add(maxConn)
        }

        val bufferSize = settings.bufferSize
        if (bufferSize.isNotBlank() && bufferSize != "16384" && (bufferSize.toIntOrNull() ?: 0) > 0) {
            args.add("-b")
            args.add(bufferSize)
        }

        val defaultTtl = settings.defaultTtl
        if (defaultTtl.isNotBlank() && (defaultTtl.toIntOrNull() ?: 0) > 0) {
            args.add("-g")
            args.add(defaultTtl)
        }

        if (settings.noDomain) {
            args.add("-N")
        }

        if (settings.noIpv6) {
            args.add("-X")
        }

        val connIp = settings.connIp
        if (connIp.isNotBlank()) {
            args.add("-I")
            args.add(connIp)
        }

        if (settings.waitSend) {
            args.add("-Z")
        }

        val awaitInt = settings.awaitInt
        if (awaitInt.isNotBlank() && (awaitInt.toIntOrNull() ?: 0) > 0) {
            args.add("-W")
            args.add(awaitInt)
        }

        if (settings.tcpFastOpen) {
            args.add("-F")
        }

        val hostsMode = settings.hostsMode
        val hosts =
            when (hostsMode) {
                "blacklist" -> settings.hostsBlacklist
                "whitelist" -> settings.hostsWhitelist
                else -> ""
            }
        if (hostsMode == "blacklist" && hosts.isNotBlank()) {
            args.add("-H")
            args.add(":$hosts")
            args.add("-A")
            args.add("none")
        } else if (hostsMode == "whitelist" && hosts.isNotBlank()) {
            args.add("-H")
            args.add(":$hosts")
        }

        val desyncHttp = settings.desyncHttp
        val desyncHttps = settings.desyncHttps
        val desyncUdp = settings.desyncUdp

        val protoList = mutableListOf<Char>()
        if (desyncHttps) {
            protoList.add('t')
        }
        if (desyncHttp) {
            protoList.add('h')
        }
        if (desyncUdp) {
            protoList.add('u')
        }
        val protoStr = protoList.joinToString(",")
        if (protoStr.isNotEmpty() && protoStr != "t,h,u") {
            args.add("-K")
            args.add(protoStr)
        }

        val desyncMethod = settings.desyncMethod
        val splitPosition = settings.splitPosition
        val splitAtHost = settings.splitAtHost

        if (desyncMethod != "none") {
            val suffix =
                if (splitAtHost) {
                    if (desyncHttps || !desyncHttp) {
                        "+s"
                    } else {
                        "+h"
                    }
                } else {
                    ""
                }
            val posArg = "$splitPosition$suffix"

            when (desyncMethod) {
                "split" -> {
                    args.add("-s")
                    args.add(posArg)
                }
                "disorder" -> {
                    args.add("-d")
                    args.add(posArg)
                }
                "fake" -> {
                    args.add("-f")
                    args.add(posArg)
                }
                "oob" -> {
                    args.add("-o")
                    args.add(posArg)
                }
                "disoob" -> {
                    args.add("-q")
                    args.add(posArg)
                }
            }
        }

        if (desyncMethod == "fake") {
            val fakeTtl = settings.fakeTtl
            if (fakeTtl.isNotBlank() && (fakeTtl.toIntOrNull() ?: 0) > 0) {
                args.add("-t")
                args.add(fakeTtl)
            }

            val fakeOffset = settings.fakeOffset
            if (fakeOffset.isNotBlank() && (fakeOffset.toIntOrNull() ?: 0) > 0) {
                args.add("-O")
                args.add(fakeOffset)
            }

            if (settings.md5sig) {
                args.add("-S")
            }

            val fakeData = settings.fakeData
            if (fakeData.isNotBlank()) {
                args.add("-l")
                args.add(fakeData)
            }

            val fakeTlsMod = settings.fakeTlsMod
            if (fakeTlsMod.isNotBlank()) {
                args.add("-Q")
                args.add(fakeTlsMod)
            }

            val tlsminor = settings.tlsminor
            if (tlsminor.isNotBlank() && (tlsminor.toIntOrNull() ?: -1) >= 0) {
                args.add("-m")
                args.add(tlsminor)
            }

            val fakeSni = settings.fakeSni
            if (fakeSni.isNotBlank()) {
                args.add("-n")
                args.add(fakeSni)
            }
        }

        if (desyncMethod == "oob" || desyncMethod == "disoob") {
            val oobData = settings.oobData.ifEmpty { "a" }
            if (oobData.isNotEmpty()) {
                val byteVal = oobData[0].code
                args.add("-e")
                args.add(String.format("\\x%02x", byteVal))
            }
        }

        val httpMods = mutableListOf<Char>()
        if (settings.hostMixedCase) {
            httpMods.add('h')
        }
        if (settings.domainMixedCase) {
            httpMods.add('d')
        }
        if (settings.hostRemoveSpaces) {
            httpMods.add('r')
        }
        if (httpMods.isNotEmpty()) {
            args.add("-M")
            args.add(httpMods.joinToString(","))
        }

        if (settings.tlsrecEnabled) {
            val tlsPos = settings.tlsrecPosition
            val atSni = settings.tlsrecAtSni
            val tlsArg =
                if (atSni) {
                    "$tlsPos+s"
                } else {
                    tlsPos
                }
            args.add("-r")
            args.add(tlsArg)
        }

        val udpFakeCount = settings.udpFakeCount
        if (udpFakeCount.isNotBlank() && (udpFakeCount.toIntOrNull() ?: 0) > 0) {
            args.add("-a")
            args.add(udpFakeCount)
        }

        if (settings.dropSack) {
            args.add("-Y")
        }

        val round = settings.round
        if (round.isNotBlank()) {
            args.add("-R")
            args.add(round)
        }

        val pf = settings.pf
        if (pf.isNotBlank()) {
            args.add("-V")
            args.add(pf)
        }

        val ipset = settings.ipset
        if (ipset.isNotBlank()) {
            args.add("-j")
            args.add(":$ipset")
        }

        val auto = settings.auto
        if (auto.isNotBlank()) {
            args.add("-A")
            args.add(auto)
        }

        val autoMode = settings.autoMode
        if (autoMode.isNotBlank() && autoMode != "none") {
            args.add("-L")
            args.add(autoMode)
        }

        val timeout = settings.timeout
        if (timeout.isNotBlank()) {
            args.add("-T")
            args.add(timeout)
        }

        return args.joinToString(" ")
    }

    fun applyCmdArgsToEngineSettings(
        argsStr: String,
        current: EngineSettings,
    ): EngineSettings {
        val trimmed = argsStr.trim()
        val firstArgIndex = trimmed.indexOf("-")
        val effectiveArgsStr =
            if (firstArgIndex >= 0) {
                trimmed.substring(firstArgIndex)
            } else {
                trimmed
            }

        val tokens = shellSplit(effectiveArgsStr)
        if (tokens.isEmpty()) {
            return current
        }

        var i = 0
        var result = current

        while (i < tokens.size) {
            val token = tokens[i]
            when {
                token == "-i" && i + 1 < tokens.size -> {
                    result = result.copy(proxyIp = tokens[++i])
                }
                token.startsWith("-i") && token.length > 2 -> {
                    result = result.copy(proxyIp = token.substring(2))
                }

                token == "-p" && i + 1 < tokens.size -> {
                    result = result.copy(proxyPort = tokens[++i])
                }
                token.startsWith("-p") && token.length > 2 -> {
                    result = result.copy(proxyPort = token.substring(2))
                }

                token == "-c" && i + 1 < tokens.size -> {
                    result = result.copy(maxConnections = tokens[++i])
                }
                token.startsWith("-c") && token.length > 2 -> {
                    result = result.copy(maxConnections = token.substring(2))
                }

                token == "-b" && i + 1 < tokens.size -> {
                    result = result.copy(bufferSize = tokens[++i])
                }
                token.startsWith("-b") && token.length > 2 -> {
                    result = result.copy(bufferSize = token.substring(2))
                }

                token == "-g" && i + 1 < tokens.size -> {
                    result = result.copy(defaultTtl = tokens[++i])
                }
                token.startsWith("-g") && token.length > 2 -> {
                    result = result.copy(defaultTtl = token.substring(2))
                }

                token == "-N" -> {
                    result = result.copy(noDomain = true)
                }

                token == "-X" -> {
                    result = result.copy(noIpv6 = true)
                }

                token == "-I" && i + 1 < tokens.size -> {
                    result = result.copy(connIp = tokens[++i])
                }
                token.startsWith("-I") && token.length > 2 -> {
                    result = result.copy(connIp = token.substring(2))
                }

                token == "-Z" -> {
                    result = result.copy(waitSend = true)
                }

                token == "-W" && i + 1 < tokens.size -> {
                    result = result.copy(awaitInt = tokens[++i])
                }
                token.startsWith("-W") && token.length > 2 -> {
                    result = result.copy(awaitInt = token.substring(2))
                }

                token == "-F" -> {
                    result = result.copy(tcpFastOpen = true)
                }

                token == "-H" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    val hostVal =
                        if (raw.startsWith(":")) {
                            raw.substring(1)
                        } else {
                            raw
                        }
                    result =
                        result.copy(
                            hostsMode = if (result.hostsMode == "none") "whitelist" else result.hostsMode,
                            hostsWhitelist = hostVal,
                        )
                }
                token.startsWith("-H") && token.length > 2 -> {
                    val raw = token.substring(2)
                    val hostVal =
                        if (raw.startsWith(":")) {
                            raw.substring(1)
                        } else {
                            raw
                        }
                    result =
                        result.copy(
                            hostsMode = if (result.hostsMode == "none") "whitelist" else result.hostsMode,
                            hostsWhitelist = hostVal,
                        )
                }

                token == "-K" && i + 1 < tokens.size -> {
                    val protos = tokens[++i].split(",").map { it.trim() }
                    result =
                        result.copy(
                            desyncHttps = protos.contains("t"),
                            desyncHttp = protos.contains("h"),
                            desyncUdp = protos.contains("u"),
                        )
                }
                token.startsWith("-K") && token.length > 2 -> {
                    val protos = token.substring(2).split(",").map { it.trim() }
                    result =
                        result.copy(
                            desyncHttps = protos.contains("t"),
                            desyncHttp = protos.contains("h"),
                            desyncUdp = protos.contains("u"),
                        )
                }

                token == "-s" && i + 1 < tokens.size -> {
                    val pos = tokens[++i]
                    result =
                        result.copy(
                            desyncMethod = "split",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }
                token.startsWith("-s") && token.length > 2 -> {
                    val pos = token.substring(2)
                    result =
                        result.copy(
                            desyncMethod = "split",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }

                token == "-d" && i + 1 < tokens.size -> {
                    val pos = tokens[++i]
                    result =
                        result.copy(
                            desyncMethod = "disorder",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }
                token.startsWith("-d") && token.length > 2 -> {
                    val pos = token.substring(2)
                    result =
                        result.copy(
                            desyncMethod = "disorder",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }

                token == "-f" && i + 1 < tokens.size -> {
                    val pos = tokens[++i]
                    result =
                        result.copy(
                            desyncMethod = "fake",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }
                token.startsWith("-f") && token.length > 2 -> {
                    val pos = token.substring(2)
                    result =
                        result.copy(
                            desyncMethod = "fake",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }

                token == "-o" && i + 1 < tokens.size -> {
                    val pos = tokens[++i]
                    result =
                        result.copy(
                            desyncMethod = "oob",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }
                token.startsWith("-o") && token.length > 2 -> {
                    val pos = token.substring(2)
                    result =
                        result.copy(
                            desyncMethod = "oob",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }

                token == "-q" && i + 1 < tokens.size -> {
                    val pos = tokens[++i]
                    result =
                        result.copy(
                            desyncMethod = "disoob",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }
                token.startsWith("-q") && token.length > 2 -> {
                    val pos = token.substring(2)
                    result =
                        result.copy(
                            desyncMethod = "disoob",
                            splitAtHost = pos.endsWith("+s") || pos.endsWith("+h"),
                            splitPosition = pos.removeSuffix("+s").removeSuffix("+h"),
                        )
                }

                token == "-t" && i + 1 < tokens.size -> {
                    result = result.copy(fakeTtl = tokens[++i])
                }
                token.startsWith("-t") && token.length > 2 -> {
                    result = result.copy(fakeTtl = token.substring(2))
                }

                token == "-O" && i + 1 < tokens.size -> {
                    result = result.copy(fakeOffset = tokens[++i])
                }
                token.startsWith("-O") && token.length > 2 -> {
                    result = result.copy(fakeOffset = token.substring(2))
                }

                token == "-S" -> {
                    result = result.copy(md5sig = true)
                }

                token == "-l" && i + 1 < tokens.size -> {
                    result = result.copy(fakeData = tokens[++i])
                }
                token.startsWith("-l") && token.length > 2 -> {
                    result = result.copy(fakeData = token.substring(2))
                }

                token == "-Q" && i + 1 < tokens.size -> {
                    result = result.copy(fakeTlsMod = tokens[++i])
                }
                token.startsWith("-Q") && token.length > 2 -> {
                    result = result.copy(fakeTlsMod = token.substring(2))
                }

                token == "-m" && i + 1 < tokens.size -> {
                    result = result.copy(tlsminor = tokens[++i])
                }
                token.startsWith("-m") && token.length > 2 -> {
                    result = result.copy(tlsminor = token.substring(2))
                }

                token == "-n" && i + 1 < tokens.size -> {
                    result = result.copy(fakeSni = tokens[++i])
                }
                token.startsWith("-n") && token.length > 2 -> {
                    result = result.copy(fakeSni = token.substring(2))
                }

                token == "-e" && i + 1 < tokens.size -> {
                    result = result.copy(oobData = parseOobChar(tokens[++i]))
                }
                token.startsWith("-e") && token.length > 2 -> {
                    result = result.copy(oobData = parseOobChar(token.substring(2)))
                }

                token == "-M" && i + 1 < tokens.size -> {
                    val mods = tokens[++i].split(",").map { it.trim() }
                    result =
                        result.copy(
                            hostMixedCase = mods.contains("h"),
                            domainMixedCase = mods.contains("d"),
                            hostRemoveSpaces = mods.contains("r"),
                        )
                }
                token.startsWith("-M") && token.length > 2 -> {
                    val mods = token.substring(2).split(",").map { it.trim() }
                    result =
                        result.copy(
                            hostMixedCase = mods.contains("h"),
                            domainMixedCase = mods.contains("d"),
                            hostRemoveSpaces = mods.contains("r"),
                        )
                }

                token == "-r" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    result =
                        result.copy(
                            tlsrecEnabled = true,
                            tlsrecAtSni = raw.endsWith("+s"),
                            tlsrecPosition = raw.removeSuffix("+s"),
                        )
                }
                token.startsWith("-r") && token.length > 2 -> {
                    val raw = token.substring(2)
                    result =
                        result.copy(
                            tlsrecEnabled = true,
                            tlsrecAtSni = raw.endsWith("+s"),
                            tlsrecPosition = raw.removeSuffix("+s"),
                        )
                }

                token == "-a" && i + 1 < tokens.size -> {
                    result = result.copy(udpFakeCount = tokens[++i])
                }
                token.startsWith("-a") && token.length > 2 -> {
                    result = result.copy(udpFakeCount = token.substring(2))
                }

                token == "-Y" -> {
                    result = result.copy(dropSack = true)
                }

                token == "-R" && i + 1 < tokens.size -> {
                    result = result.copy(round = tokens[++i])
                }
                token.startsWith("-R") && token.length > 2 -> {
                    result = result.copy(round = token.substring(2))
                }

                token == "-V" && i + 1 < tokens.size -> {
                    result = result.copy(pf = tokens[++i])
                }
                token.startsWith("-V") && token.length > 2 -> {
                    result = result.copy(pf = token.substring(2))
                }

                token == "-j" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    val ipsetVal = if (raw.startsWith(":")) raw.substring(1) else raw
                    result = result.copy(ipset = ipsetVal)
                }
                token.startsWith("-j") && token.length > 2 -> {
                    val raw = token.substring(2)
                    val ipsetVal = if (raw.startsWith(":")) raw.substring(1) else raw
                    result = result.copy(ipset = ipsetVal)
                }

                token == "-A" && i + 1 < tokens.size -> {
                    val raw = tokens[++i]
                    if (raw == "none" && result.hostsMode == "none") {
                        result = result.copy(hostsMode = "blacklist")
                    } else if (raw != "none") {
                        result = result.copy(auto = raw)
                    }
                }
                token.startsWith("-A") && token.length > 2 -> {
                    val raw = token.substring(2)
                    if (raw == "none" && result.hostsMode == "none") {
                        result = result.copy(hostsMode = "blacklist")
                    } else if (raw != "none") {
                        result = result.copy(auto = raw)
                    }
                }

                token == "-L" && i + 1 < tokens.size -> {
                    result = result.copy(autoMode = tokens[++i])
                }
                token.startsWith("-L") && token.length > 2 -> {
                    result = result.copy(autoMode = token.substring(2))
                }

                token == "-T" && i + 1 < tokens.size -> {
                    result = result.copy(timeout = tokens[++i])
                }
                token.startsWith("-T") && token.length > 2 -> {
                    result = result.copy(timeout = token.substring(2))
                }
            }
            i++
        }

        return result
    }

    private fun parseOobChar(raw: String): String {
        if (raw.startsWith("\\x") && raw.length >= 4) {
            val hex = raw.substring(2, 4)
            val code = hex.toIntOrNull(16)
            if (code != null) {
                return code.toChar().toString()
            }
        }
        return if (raw.isNotEmpty()) {
            raw.substring(0, 1)
        } else {
            "a"
        }
    }
}
