package com.fofajardo.antidpeye.data

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings()

    val json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }

    override suspend fun readFrom(input: InputStream): AppSettings =
        try {
            val content = input.readBytes().decodeToString()
            if (content.isBlank()) {
                defaultValue
            } else {
                json.decodeFromString<AppSettings>(content)
            }
        } catch (e: SerializationException) {
            defaultValue
        }

    override suspend fun writeTo(
        t: AppSettings,
        output: OutputStream,
    ) {
        val bytes = json.encodeToString(AppSettings.serializer(), t).encodeToByteArray()
        output.write(bytes)
    }
}
