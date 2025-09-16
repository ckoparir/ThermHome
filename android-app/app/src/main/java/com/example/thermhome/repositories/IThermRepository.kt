package com.example.thermhome.repositories

import com.example.thermhome.data.TempConfig
import com.example.thermhome.viewmodel.ThermStatus

interface IThermRepository {
    suspend fun delConfig(): String
    suspend fun restartMcu(): String
    suspend fun getConfig(): TempConfig
    suspend fun getStatus(): ThermStatus
    suspend fun postConfig(config: TempConfig): String
}