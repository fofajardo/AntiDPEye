package com.fofajardo.antidpeye.services

import com.fofajardo.antidpeye.data.AppStatus
import com.fofajardo.antidpeye.data.Mode

var appStatus = AppStatus.Halted to Mode.VPN
    private set

fun setStatus(
    status: AppStatus,
    mode: Mode,
) {
    appStatus = status to mode
}
