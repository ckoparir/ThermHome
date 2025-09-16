/**
 * @file therm_config.h
 * @author Caglar KOPARIR (ckoparir@gmail.com)
 * @brief
 * Some configuration functions, structs and
 * definitions for scheduling the working times
 * of thermostat.
 * @version 0.1
 * @date 2021-10-20
 *
 * @copyright Copyright (c) 2021
 *
 */

#ifndef THERM_CONFIG_H
#define THERM_CONFIG_H

 //#define TIME_ZONE 10800		  						// GMT+3 (3 * 60 * 60)
#define LOG_FILE "debug.log"	  						// log file for debugging
#define CONF_FILE "config.conf"							// SPIFFS filename
#define FACTORY_FILE "factory.conf"					// SPIFFS filename
#define NTP_SERVER1 "pool.ntp.org" 					// worldwide
#define NTP_SERVER2 "0.tr.pool.ntp.org" 		// TR1 
#define NTP_SERVER3 "1.tr.pool.ntp.org" 		// TR2 

#define LOG_SIZE 256
#define MAX_SCHEDULE 5

#include <LittleFS.h>
#include <ArduinoJson.h>
#include <ESP8266WebServer.h>

struct DateTimeData
{
	int hrs, min;
};

typedef struct Schedule
{
	DateTimeData end;
	DateTimeData start;
} Schedule;

typedef struct TempSchedule
{
	float set;
	bool scheduled;
	Schedule schedule;
} TempSchedule;

typedef struct TempConfig
{
	float rate;
	uint8_t size;
	char ssid[24];
	long timezone;
	bool ofline_mode;
	uint8_t ap_hidden;
	char ssid_pwd[24];
	long daylight_saving;
	float temp_sens_rate;
	TempSchedule* temp_schedule;
} TempConfig;

typedef struct Status
{
	bool relayon;
	uint8_t status;
	float set_current;
	float temp_current;
	int current_record;
	bool scheduled_current;
} Status;

typedef struct
{
	Status* status;
	TempConfig* config;
	ESP8266WebServer* server;
} TERM;

bool initConfig(TempConfig* cfg);
bool saveConfig(TempConfig* cfg);
bool loadConfig(TempConfig* cfg);
String readConfig(TempConfig* cfg);
bool writeConfig(TempConfig* cfg, String data);

#endif