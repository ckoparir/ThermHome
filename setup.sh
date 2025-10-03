#!/bin/bash

# ThermHome setup script for wifi-client and wifi-server
# Run with: bash setup.sh

set -e

# ---- Helper functions ----
update_ini() {
    local ini_file="$1"
    local var="$2"
    local val="$3"
    sed -i "s|^$var *=.*|$var = $val|" "$ini_file"
}

replace_in_file() {
    local file="$1"
    local search="$2"
    local replace="$3"
    sed -i "s|$search|$replace|g" "$file"
}

# ---- wifi-client setup ----
echo "=== WiFi Client Setup ==="
read -rp "Enter upload port for wifi-client (e.g. /dev/ttyUSB0): " WIFICLIENT_PORT
read -rp "Enter WiFi SSID for wifi-client: " WIFICLIENT_SSID
read -rp "Enter WiFi password for wifi-client: " WIFICLIENT_PASS
read -rp "Enter server name (IP or hostname) for wifi-client: " WIFICLIENT_SERVER

update_ini "wifi-client/platformio.ini" "upload_port" "$WIFICLIENT_PORT"
replace_in_file "wifi-client/src/main.cpp" 'const char\* ssid = "[^"]*";' "const char* ssid = \"$WIFICLIENT_SSID\";"
replace_in_file "wifi-client/src/main.cpp" 'const char\* password = "[^"]*";' "const char* password = \"$WIFICLIENT_PASS\";"
replace_in_file "wifi-client/src/main.cpp" 'const char\* serverName = "[^"]*";' "const char* serverName = \"$WIFICLIENT_SERVER\";"

echo "wifi-client setup complete."

# ---- wifi-server setup ----
echo "=== WiFi Server Setup ==="
read -rp "Enter upload port for wifi-server (e.g. /dev/ttyUSB1): " WIFISERVER_PORT
read -rp "Enter WiFi SSID for wifi-server: " WIFISERVER_SSID
read -rp "Enter WiFi password for wifi-server: " WIFISERVER_PASS
read -rp "Enter WiFi gateway for wifi-server: " WIFISERVER_GATEWAY
read -rp "Enter NTP_SERVER1 (default: pool.ntp.org): " NTP1
read -rp "Enter NTP_SERVER2 (default: time.nist.gov): " NTP2
read -rp "Enter NTP_SERVER3 (default: time.windows.com): " NTP3
read -rp "Enter timezone for wifi-server (e.g. Europe/Istanbul): " WIFISERVER_TZ

update_ini "wifi-server/platformio.ini" "upload_port" "$WIFISERVER_PORT"
replace_in_file "wifi-server/include/therm_util.h" '#define SSID "[^"]*"' "#define SSID \"$WIFISERVER_SSID\""
replace_in_file "wifi-server/include/therm_util.h" '#define SSID_PASS "[^"]*"' "#define SSID_PASS \"$WIFISERVER_PASS\""
replace_in_file "wifi-server/include/therm_util.h" '#define GATEWAY "[^"]*"' "#define GATEWAY \"$WIFISERVER_GATEWAY\""
replace_in_file "wifi-server/include/therm_config.h" '#define NTP_SERVER1 "[^"]*"' "#define NTP_SERVER1 \"$NTP1\""
replace_in_file "wifi-server/include/therm_config.h" '#define NTP_SERVER2 "[^"]*"' "#define NTP_SERVER2 \"$NTP2\""
replace_in_file "wifi-server/include/therm_config.h" '#define NTP_SERVER3 "[^"]*"' "#define NTP_SERVER3 \"$NTP3\""
replace_in_file "wifi-server/data/factory.conf" 'ssid=.*' "ssid=$WIFISERVER_SSID"
replace_in_file "wifi-server/data/factory.conf" 'ssid_pwd=.*' "ssid_pwd=$WIFISERVER_PASS"
replace_in_file "wifi-server/data/factory.conf" 'timezone=.*' "timezone=$WIFISERVER_TZ"

echo "wifi-server setup complete."

echo "All done! You can now build and upload using PlatformIO CLI."
