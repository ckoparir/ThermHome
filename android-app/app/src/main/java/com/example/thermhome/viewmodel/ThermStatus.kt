package com.example.thermhome.viewmodel

import kotlinx.serialization.Serializable

@Serializable
data class ThermStatus(
    var set: Float = 0F,
    var temp: Float = 0F,
    var current_record: Int = -1,
    var relayon: Boolean = false,
    var scheduled: Boolean = false,
)