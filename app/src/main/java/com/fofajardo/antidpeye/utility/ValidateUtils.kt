package com.fofajardo.antidpeye.utility

import android.net.InetAddresses
import android.os.Build

fun checkIp(ip: String): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        InetAddresses.isNumericAddress(ip)
    } else {
        true
    }

fun checkNotLocalIp(ip: String): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        InetAddresses.isNumericAddress(ip) &&
            InetAddresses.parseNumericAddress(ip).let {
                !it.isAnyLocalAddress && !it.isLoopbackAddress
            }
    } else {
        true
    }
