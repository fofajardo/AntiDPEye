package com.fofajardo.antidpeye.core

object TProxyService {
    init {
        System.loadLibrary("hev-socks5-tunnel")
    }

    @JvmStatic
    external fun TProxyStartService(configPath: String, fd: Int): Boolean

    @JvmStatic
    external fun TProxyIsRunning(): Boolean

    @JvmStatic
    external fun TProxyStopService(): Boolean

    @JvmStatic
    @Suppress("unused")
    external fun TProxyGetStats(): LongArray
}
