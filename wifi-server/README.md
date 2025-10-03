![PlatformIO](https://img.shields.io/badge/platformio-ready-orange)
![License](https://img.shields.io/github/license/ckoparir/ThermHome)

# ThermHome WiFi Server

This is the firmware for the ESP8266 device acting as a WiFi server in the ThermHome project.

## Preparation

Before building or uploading, configure the following:
- `platformio.ini`: Set `upload_port` for your ESP8266 board.
- `include/therm_util.h`: Set `SSID`, `SSID_PASS`, and `GATEWAY` for your WiFi setup.
- `include/therm_config.h`: (Optional) Set `NTP_SERVER1`, `NTP_SERVER2`, `NTP_SERVER3` if you want to change NTP servers.
- `data/factory.conf`: Set `ssid`, `ssid_pwd`, and `timezone` for initial device configuration.

## Building & Uploading

```sh
# Enter the wifi-server directory
cd wifi-server

# Build the firmware
platformio run

# Upload the firmware to your ESP8266
platformio run --target upload
```
> *You can also use the PlatformIO extension in VSCode for building and uploading.*

> **Notes:** Make sure to update your WiFi credentials and server information before uploading.
See the root [README.md](../README.md) for project-wide details and setup scripts (if available).
