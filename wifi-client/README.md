# ThermHome WiFi Client

This is the firmware for the ESP8266 device acting as a WiFi client in the ThermHome project.

## Preparation

Before building or uploading, edit the following:
- `platformio.ini`: Set `upload_port` to match your device's port.
- `src/main.cpp`: Set `ssid`, `password`, and `serverName` to match your WiFi and server setup.

## Building & Uploading

```sh
# Enter the wifi-client directory
cd wifi-client

# Build the firmware
platformio run

# Upload the firmware to your ESP8266
platformio run --target upload
```
> *You can also use the PlatformIO extension in VSCode for building and uploading.*

> **Notes:** Make sure to update your WiFi credentials and server information before uploading.
See the root [README.md](../README.md) for project-wide details and setup scripts (if available).
