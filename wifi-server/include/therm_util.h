/**
 * @file therm_util.h
 * @author Caglar KOPARIR(ckoparir@gmail.com)
 * @brief utility library for esp8266 for the
 * project uses WiFi communication to be able to
 * send REST api to clients which request tempature
 * data for WiFi relay
 * @version 0.1
 * @date 2021-10-18
 *
 * @copyright CTech Copyright (c) 2021
 *
 */
#ifndef THERM_UTIL_H
#define THERM_UTIL_H

#include <WiFiClient.h>
#include <ESP8266WiFi.h>
#include <ArduinoJson.h>
#include "therm_config.h"

#define STA_PIN 16
#define TMP_PIN A0
#define LED_PIN D2

#define NAPT 1000
#define NAPT_PORT 80 
#define SERVER_PORT 80
#define SSID "YOUR_SSID"
#define SSID_PASS "YOUR_ROUTER_PASSWORD"
#define GATEWAY IPAddress(192, 168, 1, 1)
#define SUBNET IPAddress(255, 255, 255, 0)
#define LOCAL_IP IPAddress(192, 168, 1, 110)

#define DBG_CONNECTED 0
#define DBG_NOT_CONNECTED 1
#define DBG_CLIENT_CONNECTED 2
#define DBG_OFFLINE 3
#define DBG_ERROR_READING 4
#define DBG_NO_SCHEDULE 5
#define DBG_NTP_NOT_CONNECTED 6

void setTemp();
void getTemp();
TERM* initTerm();
void getStatus();
void serverLoop();
void getConfig();
void setConfig();
void restartMCU();
void initApMode();
bool initStaMode();
void resetConfig();
void handleNotFound();
void restServerRouting();
void blinkConnectionLed();
DateTimeData getDateTime();
void debug(uint8_t status);
bool calcTempForRelay(DateTimeData now);
bool isTimeWithin(DateTimeData start, DateTimeData stop, DateTimeData query);

#endif
