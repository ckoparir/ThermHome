/**
 * @file therm_config.cpp
 * @author Caglar KOPARIR (ckoparir@gmail.com)
 * @brief
 * Some configuration functions, structs and
 * definitions for scheduling the working times
 * of thermostat.
 * @version 0.1
 * @date 2021-10-20
 *
 * @copyright Copyright (c) 2021
 */

#include "therm_config.h"

bool initConfig(TempConfig* cfg) {
    if (!LittleFS.begin()) {
	Serial.println("Failed to mount file system...!");
	return false;
    }

    if (!loadConfig(cfg)) {
	Serial.printf("Error reading config file...!\n");
	return false;
    }
    return true;
}

bool saveConfig(TempConfig* cfg) {
    bool result = false;
    StaticJsonDocument<2048> doc;

    doc["size"] = cfg->size;
    doc["rate"] = cfg->rate;
    doc["ssid"] = cfg->ssid;
    doc["ssid_pwd"] = cfg->ssid_pwd;
    doc["timezone"] = cfg->timezone;
    doc["ap_hidden"] = cfg->ap_hidden;
    doc["offline_mode"] = cfg->ofline_mode;
    doc["temp_sens_rate"] = cfg->temp_sens_rate;
    doc["daylight_saving"] = cfg->daylight_saving;
    doc["temp_schedule"] = doc.createNestedArray("temp_schedule");

    Serial.printf("\ndoc-UP:\n%s\n", doc.as<String>().c_str());

    for (uint8_t i = 0; i < cfg->size; i++) {
	doc["temp_schedule"][i]["set"] = cfg->temp_schedule[i].set;
	doc["temp_schedule"][i]["scheduled"] = cfg->temp_schedule[i].scheduled;

	doc["temp_schedule"][i]["schedule"] =
	    doc.createNestedObject("schedule");

	JsonObject start =
	    doc["temp_schedule"][i]["schedule"].createNestedObject("start");
	JsonObject end =
	    doc["temp_schedule"][i]["schedule"].createNestedObject("end");

	start["hrs"] = cfg->temp_schedule[i].schedule.start.hrs;
	start["min"] = cfg->temp_schedule[i].schedule.start.min;

	end["hrs"] = cfg->temp_schedule[i].schedule.end.hrs;
	end["min"] = cfg->temp_schedule[i].schedule.end.min;
    }

    File file = LittleFS.open(CONF_FILE, "w");
    if (!file) {
	Serial.println("Failed to open config file for writing...!");
	return false;
    }
    if (serializeJson(doc, file) == 0)
	Serial.printf("Config file could not be written...!\n");
    else
	result = true;

    delay(5);
    file.close();
    return result;
}

bool loadConfig(TempConfig* cfg) {
    StaticJsonDocument<2048> doc;
    File file = LittleFS.open(CONF_FILE, "r");

    if (!file) {
	Serial.println("Trying to load factory configuration...!");
	file = LittleFS.open(FACTORY_FILE, "r");
	delay(2000);
	if (!file) {
	    Serial.println("No saved data...!");
	    return false;
	}
    }

    DeserializationError error = deserializeJson(doc, file);

    file.close();

    if (error.code() != DeserializationError::Ok) {
	Serial.printf("Deserialization error: %s", error.c_str());
	return false;
    }

    cfg->size = doc["size"];
    cfg->rate = doc["rate"];
    strcpy(cfg->ssid, doc["ssid"]);
    strcpy(cfg->ssid_pwd, doc["ssid_pwd"]);
    cfg->temp_sens_rate = doc["temp_sens_rate"];
    cfg->timezone = doc["timezone"];
    cfg->ap_hidden = doc["ap_hidden"];
    cfg->ofline_mode = doc["offline_mode"];
    cfg->daylight_saving = doc["daylight_saving"];

    for (uint8_t i = 0; i < cfg->size; i++) {
	cfg->temp_schedule[i].scheduled = doc["temp_schedule"][i]["scheduled"];
	cfg->temp_schedule[i].set = doc["temp_schedule"][i]["set"];

	cfg->temp_schedule[i].schedule.start.hrs =
	    doc["temp_schedule"][i]["start"]["hrs"];
	cfg->temp_schedule[i].schedule.start.min =
	    doc["temp_schedule"][i]["start"]["min"];
	cfg->temp_schedule[i].schedule.end.hrs =
	    doc["temp_schedule"][i]["end"]["hrs"];
	cfg->temp_schedule[i].schedule.end.min =
	    doc["temp_schedule"][i]["end"]["min"];
    }
    return true;
}

String readConfig(TempConfig* cfg) {
    String result = "";
    loadConfig(cfg);
    delay(5);

    File file = LittleFS.open(CONF_FILE, "r");

    if (!file) {
	Serial.println("Trying to load factory configuration...!");
	file = LittleFS.open(FACTORY_FILE, "r");
	delay(500);
	if (!file) {
	    Serial.println("No saved data...!");
	    return result;
	}
    }

    result = file.readString();

    delay(5);

    file.close();
    return result;
}

bool writeConfig(TempConfig* cfg, String data) {
    bool result = false;
    File file = LittleFS.open(CONF_FILE, "w+");
    if (!file)
	Serial.println("Failed to write data to config file...!");
    else {
	file.printf("%s", data.c_str());
	delay(5);
	file.close();
	result = true;
    }
    return result;
}
