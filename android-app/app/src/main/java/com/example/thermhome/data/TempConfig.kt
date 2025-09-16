package com.example.thermhome.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TempConfig(
    @SerialName("ap_hidden")
    var apHidden: Int = 0,
    @SerialName("daylight_saving")
    var daylightSaving: Int = 0,
    @SerialName("offline_mode")
    var offlineMode: Boolean = false,
    @SerialName("rate")
    var rate: Float = 0.0f,
    @SerialName("size")
    var size: Int = 0,
    @SerialName("ssid")
    var ssid: String = "",
    @SerialName("ssid_pwd")
    var ssidPwd: String = "",
    @SerialName("temp_sens_rate")
    var tempSensRate: Float = 0.0f,
    @SerialName("timezone")
    var timezone: Int = 0,
    @SerialName("temp_schedule")
    var tempSchedule: ArrayList<TempSchedule> = ArrayList(),
) {
    @Serializable
    data class TempSchedule(
        @SerialName("set")
        var `set`: Double = 20.0,
        @SerialName("scheduled")
        var scheduled: Boolean = false,
        @SerialName("start")
        var start: DateTimeData = DateTimeData(),
        @SerialName("end")
        var end: DateTimeData = DateTimeData(),
    ) {
        @Serializable
        data class DateTimeData(
            @SerialName("hrs")
            var hrs: Int = 0,
            @SerialName("min")
            var min: Int = 0,
        )
    }
}