/**
 * @file main.cpp
 * @author Caglar KOPARIR (ckoparir@gmail.com)
 * @brief
 * Esp8266 WiFi thermostat to be able to communicate
 * with server by using REST API
 * @version 0.2
 * @date 2021-10-22
 *
 * @copyright Copyright (c) 2021
 *
 */

#include <WiFiClient.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

#define RELAY_PIN D0 // relay on/off

WiFiClient wifiClient;
const char* ssid = "YOUR_SSID";
const char* password = "YOUR_ROUTER_PASSWORD";
String serverName = "http://192.168.1.110:80/"; // IP ADDR FROM YOUR ROUTER AS CLIENT 

void setup()
{

  Serial.begin(115200);

  pinMode(RELAY_PIN, OUTPUT);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(1000);
    Serial.print("Connecting..");
  }

  Serial.println("IP Address: ");
  Serial.println(WiFi.localIP());
}

void loop()
{

  if (WiFi.status() == WL_CONNECTED)
  {
    HTTPClient http;

    http.begin(wifiClient, serverName + "status"); // Specify request destination
    int httpCode = http.GET();                     // Send the request

    if (httpCode > 0)
    {

      // String payload = http.getString(); // Get the request response payload
      // Serial.println(payload);           // Print the response payload

      // Parse response
      DynamicJsonDocument doc(2048);
      deserializeJson(doc, http.getStream());

      // Read values
      bool relayOn = doc["relayon"].as<bool>();
      digitalWrite(RELAY_PIN, relayOn);
      Serial.println(relayOn);
    }

    http.end(); // Close connection
  }

  delay(10000); // Send a request every 30 seconds
}
