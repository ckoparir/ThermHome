/**
 * @file therm_util.c
 * @author caglar KOPARIR (ckoparir@gmail.com)
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

#include "therm_util.h"
#include <lwip/napt.h>
#include <lwip/dns.h>

static TERM* Term;
static unsigned long ms;
static unsigned long lastMs;

TERM* initTerm()
{
	Serial.begin(115200);

	lastMs = 0;
	ms = millis();

	Serial.println("Initializing Server");
	Term = (TERM*)calloc(1, sizeof(TERM));
	Term->status = (Status*)calloc(1, sizeof(Status));
	Term->config = (TempConfig*)calloc(1, sizeof(TempConfig));
	Term->config->temp_schedule = (TempSchedule*)calloc(MAX_SCHEDULE, sizeof(TempSchedule));

	pinMode(LED_PIN, OUTPUT);
	pinMode(STA_PIN, OUTPUT);

	Term->server = new ESP8266WebServer(SERVER_PORT);

	debug(DBG_NOT_CONNECTED);
	initConfig(Term->config);

	if (initStaMode()) Serial.println("Connected to Wifi AP");

	// Set server routing
	restServerRouting();

	// Set not found response
	Term->server->onNotFound(handleNotFound);

	// Start server
	Term->server->begin();
	delay(1000);
	// initApMode();
	initStaMode();
	if (WiFi.status() == WL_CONNECTED)
		configTime(Term->config->timezone, Term->config->daylight_saving, NTP_SERVER1, NTP_SERVER2, NTP_SERVER3);
	else
	{
		Serial.printf("Wifi STA is Offline");
		debug(DBG_OFFLINE);
	}

	ESP.wdtDisable();

	return Term;
}

void initApMode()
{
	auto& server = WiFi.softAPDhcpServer();
	// if (WiFi.status() == WL_CONNECTED)
	{
		server.setDns(WiFi.dnsIP());
		server.setRouter(true);
	}

	WiFi.softAPConfig(LOCAL_IP, GATEWAY, SUBNET);

	WiFi.softAP(SSID, SSID_PASS, 1, Term->config->ap_hidden, 5);
	Serial.printf("AP: %s\n", WiFi.softAPIP().toString().c_str());
	Serial.printf("\nServer IP: %s and DNS: %s\n", WiFi.localIP().toString().c_str(), WiFi.dnsIP().toString().c_str());

	err_t ret = ip_napt_init(NAPT, NAPT_PORT);
	Serial.printf("ip_napt_init(%d,%d): ret=%d (OK=%d)\n", NAPT, NAPT_PORT, (int)ret, (int)ERR_OK);

	if (ret == ERR_OK) {
		ret = ip_napt_enable_no(SOFTAP_IF, 1);
		Serial.printf("ip_napt_enable_no(SOFTAP_IF): ret=%d (OK=%d)\n", (int)ret, (int)ERR_OK);
		if (ret == ERR_OK) { Serial.printf("WiFi Network '%s' with same password is now NATed behind '%s'\n", SSID, SSID_PASS); }
	}
}

bool initStaMode()
{
	const uint8_t MAX_RETRY = 10;
	WiFi.mode(WIFI_STA);
	// WiFi.beginSmartConfig();
	// WiFi.begin(Term->config->ssid, Term->config->ssid_pwd);
	WiFi.config(LOCAL_IP, GATEWAY, SUBNET, GATEWAY);
	WiFi.begin(SSID, SSID_PASS, 11);
	delay(1000);

	uint8_t retry = 0;

	// Wait for connection
	while (WiFi.status() != WL_CONNECTED)
	{
		retry++;
		// delay(1000);
		blinkConnectionLed();
		debug(DBG_NOT_CONNECTED);
		Serial.println("Cannot connect Wifi AP");
		if (retry > MAX_RETRY) break;
	}
	if (retry > MAX_RETRY)
	{
		debug(DBG_OFFLINE);
		return false;
	}

	debug(DBG_CONNECTED);
	return true;
}

void blinkConnectionLed()
{
	for (size_t i = 0; i < 5; i++)
	{
		digitalWrite(LED_PIN, 1);
		delay(250);
		digitalWrite(LED_PIN, 0);
		delay(250);
	}
}

void swap(int* xp, int* yp)
{
	int temp = *xp;
	*xp = *yp;
	*yp = temp;
}

void bubbleSort(int arr[], int n)
{
	int i, j;
	for (i = 0; i < n - 1; i++)
		for (j = 0; j < n - i - 1; j++)
			if (arr[j] > arr[j + 1])
				swap(&arr[j], &arr[j + 1]);
}

void getTemp()
{
	/*
		int val = 0;
		float median = 0;
		const int repeat = 10;
		int readings[repeat];

		for (uint8_t i = 0; i < repeat; i++)
		{
			readings[i] = analogRead(TMP_PIN);
			delay(100);
		}

		bubbleSort(readings, repeat);

		for (uint8_t i = 2; i < repeat - 2; i++)
			val += readings[i];


		median = val / (repeat - 4);
		float celsius = median * Term->config->rate;

	uint8_t value = analogRead(TMP_PIN);
	float volt = (value / 1023.0) * 3300 * Term->config->rate;
	float celsius = volt / 10.0;
	*/
	int Vo;
	float R1 = 10000;
	float logR2, R2, T, Tc, Tf, Tf2;
	float c1 = 1.009249522e-03, c2 = 2.378405444e-04, c3 = 2.019202697e-07;

	Vo = system_adc_read();
	R2 = R1 * (1023.0 / (float)Vo - 1.0);
	logR2 = log(R2);
	T = (1.0 / (c1 + c2 * logR2 + c3 * logR2 * logR2 * logR2));
	Tc = T - 273.15;
	Tf = (Tc * 9.0) / 5.0 + 32.0;
	Tf2 = (Tc * 9.0) / 3.3 + 32.0;

	if (isnan(Tc))
	{
		Serial.println(F("Error reading temperature!"));
		debug(DBG_ERROR_READING);
	}
	else
	{
		Term->status->temp_current = Tc * (Term->config->rate);
		Serial.printf("Value: %d Coef: %.3f  => Temp: %.3f F - %.3f F - %.3f \u2103\n", Vo, T, Tf, Tf2, Tc);
	}
}

// Define routing
void restServerRouting()
{
	Term->server->on(F("/status"), HTTP_GET, getStatus);
	Term->server->on(F("/config"), HTTP_GET, getConfig);
	Term->server->on(F("/config"), HTTP_POST, setConfig);
	Term->server->on(F("/config"), HTTP_DELETE, resetConfig);
	Term->server->on(F("/restart"), HTTP_PUT, restartMCU);
}

void getStatus()
{
	loadConfig(Term->config);
	delay(5);
	DynamicJsonDocument doc(256);
	doc["status"] = Term->status->status;
	doc["relayon"] = Term->status->relayon;
	doc["set"] = Term->status->set_current;
	doc["temp"] = Term->status->temp_current;
	doc["scheduled"] = Term->status->scheduled_current;
	doc["current_record"] = Term->status->current_record;
	Term->server->send(200, "text/json", doc.as<String>());
	debug(DBG_CLIENT_CONNECTED);
}

void restartMCU()
{
	Term->server->send(200, "Restarting MCU...", "OK");
	delay(2000);
	ESP.restart();
}

void resetConfig()
{
	if (LittleFS.exists(CONF_FILE)) {
		if (LittleFS.remove(CONF_FILE))
			Term->server->send(200, "text", "OK");
		return;
	}
	Term->server->send(400, "text/json",
		"{\"response\": \"Invalid Request\", \"code\": 400}");
}


void getConfig()
{
	String json = readConfig(Term->config);
	if (json.length() > 0)
	{
		Term->server->send(200, "text/json", json);
		debug(DBG_CLIENT_CONNECTED);
	}
	else
		Term->server->send(400, "text/json", "{\"response\": \"Invalid Request\", \"code\": 400}");
}

void setConfig()
{
	String data = Term->server->arg("plain");

	if (writeConfig(Term->config, data))
		Term->server->send(200, "text", "OK\n");
	else
		Term->server->send(500, "text", " Internal Server Error\n");
}

bool isTimeWithin(DateTimeData start, DateTimeData stop, DateTimeData query)
{
	uint16_t startInMins = (60 * start.hrs + start.min);
	uint16_t stopInMins = (60 * stop.hrs + stop.min);
	uint16_t queryInMins = (60 * query.hrs + query.min);

	return ((startInMins <= queryInMins) && (queryInMins <= stopInMins));
}

// LM35 read & proc
bool calcTempForRelay(DateTimeData now)
{
	getTemp();

	int currentRecord = -1;
	bool inSchedule = false;
	float setTemp = Term->config->temp_schedule[0].set;

	for (uint8_t i = 0; i < Term->config->size; i++)
	{
		DateTimeData shrs = Term->config->temp_schedule[i].schedule.start;
		DateTimeData ehrs = Term->config->temp_schedule[i].schedule.end;

		if (isTimeWithin(shrs, ehrs, now) && Term->config->temp_schedule[i].scheduled)
		{
			currentRecord = i;
			inSchedule = true;
			setTemp = Term->config->temp_schedule[i].set;
			break;
		}
	}

	Term->status->scheduled_current = inSchedule;
	Term->status->current_record = currentRecord;

	if (inSchedule)
	{
		Term->status->set_current = setTemp;
		if (Term->status->temp_current > setTemp + Term->config->temp_sens_rate)
		{
			digitalWrite(STA_PIN, 0);
			Term->status->relayon = false;
		}
		else if (Term->status->temp_current < setTemp - Term->config->temp_sens_rate)
		{
			digitalWrite(STA_PIN, 1);
			Term->status->relayon = true;
		}
	}
	else
	{
		digitalWrite(STA_PIN, 0);
		Term->status->relayon = false;
	}
	return inSchedule;
}

void debug(uint8_t status)
{
	Term->status->status = status;
	if (status != (DBG_NOT_CONNECTED | DBG_OFFLINE))
		digitalWrite(LED_PIN, 1);
	else
		digitalWrite(LED_PIN, 0);
}

// Manage not found URL
void handleNotFound()
{
	String message = "File Not Found\n\n";
	message += "URI: ";
	message += Term->server->uri();
	message += "\nMethod: ";
	message += (Term->server->method() == HTTP_GET) ? "GET" : "POST";
	message += "\nArguments: ";
	message += Term->server->args();
	message += "\n";
	for (uint8_t i = 0; i < Term->server->args(); i++)
	{
		message += " " + Term->server->argName(i) + ": " + Term->server->arg(i) + "\n";
	}
	Term->server->send(404, "text/plain", message);
}

void serverLoop()
{
	Term->server->handleClient();
	if (millis() - ms > 5 * 1000L)
	{
		ESP.wdtFeed();
		// Connect NTP server and get current datetime
		ms = millis();
		DateTimeData dt = getDateTime();
		if (!dt.hrs && !dt.min)
		{
			debug(DBG_NTP_NOT_CONNECTED);
			Serial.println();
			Serial.println("Failed to get time from server, retry.");
			Serial.println();
			delay(500);
		}
		// else
		{
			// Get current temperature data
			if (!calcTempForRelay(dt))
			{
				Serial.println();
				Serial.printf("there is no schedule at this time.\n");
				Serial.println();
			}
		}
	}
}

DateTimeData getDateTime()
{
	DateTimeData dt = {};
	if (WiFi.status() != WL_CONNECTED) return dt;

	struct tm timeinfo;

	configTime(Term->config->timezone, Term->config->daylight_saving, NTP_SERVER1, NTP_SERVER2, NTP_SERVER3);
	delay(50);

	if (!getLocalTime(&timeinfo))
	{
		if (Term->config->ofline_mode) {
			dt.hrs = Term->config->temp_schedule[0].schedule.start.hrs;
			dt.min = Term->config->temp_schedule[0].schedule.start.min + 1;
		}
		return dt;
	}

	dt.hrs = timeinfo.tm_hour;
	dt.min = timeinfo.tm_min;
	// Serial.printf("Time: %d:%d - [ %d:%d ]\n", dt.hrs, dt.min, timeinfo.tm_hour, timeinfo.tm_min);

	return dt;
}