package com.example.thermhome.repositories

import android.util.Log
import com.example.thermhome.constants.App.TAG
import com.example.thermhome.constants.HttpRoutes
import com.example.thermhome.data.TempConfig
import com.example.thermhome.viewmodel.ThermStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ThermRepository : IThermRepository {

    private val serializer: Json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private fun get(httpRoute: String): String {
        return try {
            URL(httpRoute).readText()
        } catch (e: Exception) {
            ""
        }
    }

    private fun send(httpRoute: String, json: String?, method: String = "POST"): String {
        var result = ""
        val url = URL(httpRoute)

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.doOutput = true

        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Accept", "application/json")

        try {
            if (json != null)
                DataOutputStream(conn.outputStream).use { it.writeBytes(json) }

            BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    result += line
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            result = ""
        }
        return result
    }

    override suspend fun postConfig(config: TempConfig): String {
        return runBlocking {
            withContext(Dispatchers.Default) {
                val json = serializer.encodeToString(config)
                send(HttpRoutes.CONFIG, json)
            }
        }
    }

    override suspend fun getStatus(): ThermStatus {
        try {
            val json = runBlocking {
                get(HttpRoutes.STATUS)
            }
            if (json.isNotEmpty())
                return serializer.decodeFromString(json)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return ThermStatus()
    }

    override suspend fun getConfig(): TempConfig {

        try {
            val json = runBlocking {
                get(HttpRoutes.CONFIG)
            }

            if (json.isNotEmpty()) {
                val config = serializer.decodeFromString<TempConfig>(json)
                val obj = serializer.parseToJsonElement(json)
                val tempSchedule = obj.jsonObject["temp_schedule"] as JsonArray
                for (schedules in tempSchedule) {
                    val schedule = serializer.decodeFromJsonElement<TempConfig.TempSchedule>(schedules)
                    config.tempSchedule[tempSchedule.indexOf(schedules)] = schedule
                }
                return config
            } else
                Log.d(TAG, "Cannot serialize json...!")

        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return TempConfig()
    }

    override suspend fun restartMcu(): String {
        return runBlocking {
            withContext(Dispatchers.Default) { send(HttpRoutes.RESTART, null, "PUT") }
        }
    }

    override suspend fun delConfig(): String {
        return runBlocking {
            withContext(Dispatchers.Default) { send(HttpRoutes.CONFIG, null, "DELETE") }
        }
    }
}