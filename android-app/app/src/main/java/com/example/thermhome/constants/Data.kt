package com.example.thermhome.constants

object HttpRoutes {
    private const val HOST_IP = "192.168.1.150"
    private const val BASE_URL = "http://$HOST_IP"
    const val STATUS = "$BASE_URL/status"
    const val CONFIG = "$BASE_URL/config"
    const val RESTART = "$BASE_URL/restart"
}

object App {
    const val TAG = "MyApp"
    const val PERMISSION_REQUEST_CODE = 999
}
