package com.fofajardo.antidpeye.data

const val STARTED_BROADCAST = "com.fofajardo.antidpeye.STARTED"
const val STOPPED_BROADCAST = "com.fofajardo.antidpeye.STOPPED"
const val FAILED_BROADCAST = "com.fofajardo.antidpeye.FAILED"

const val SENDER = "sender"

enum class Sender(
    val senderName: String,
) {
    Proxy("Proxy"),
    VPN("VPN"),
}
