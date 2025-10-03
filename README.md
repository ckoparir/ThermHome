# ThermHome

ESP8266 Wifi IoT Project with Android Application

## Table of Contents
- [About](#about)
- [Project Structure](#project-structure)
- [Preparation](#preparation)
- [Requirements](#requirements)
- [Installation](#installation)
- [Building & Uploading](#building--uploading)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

---

## About

ThermHome is an IoT project for ESP8266 microcontrollers, enabling WiFi-based temperature monitoring and control, with an accompanying Android application.

## Project Structure

```
.
├── wifi-client/   
├── wifi-server/   
├── .gitignore
├── LICENSE
├── README.md
```

- `wifi-client/`: ESP8266 Client firmware source code  
- `wifi-server/`: ESP8266 Server firmware source code  

## Preparation

Before building, you must update certain configuration parameters for your WiFi environment and board:

| File                               | Property(s)                      |
|-------------------------------------|----------------------------------|
| wifi-client/platformio.ini          | upload_port                      |
| wifi-client/main.cpp                | ssid, password, serverName       |
| wifi-server/include/therm_util.h    | SSID, SSID_PASS, GATEWAY         |
| wifi-server/include/therm_config.h  | NTP_SERVER1, NTP_SERVER2, NTP_SERVER3 |
| wifi-server/data/factory.conf       | ssid, ssid_pwd, timezone         |

See the [README.md](wifi-client/README.md) and [README.md](wifi-server/README.md) in each subproject for more details.

### Using the setup.sh automation script

You can automate the configuration of these parameters by running the provided setup script:

```sh
./setup.sh
```
This script will prompt you for your WiFi credentials, server settings, and other required values, then update all necessary files for both the client and server projects.
>  **Windows users:** *Please use Git Bash to run setup.sh for best compatibility.*

## Requirements

- [PlatformIO](https://platformio.org/) (recommended via VSCode extension)
- Python 3.x
- Supported ESP8266 board (e.g., NodeMCU, Wemos D1 mini)

## Installation

1. **Install PlatformIO:**

   You can install PlatformIO using your preferred method:

   - **VSCode Extension:**  
     [VSCode Extension Guide](https://platformio.org/install/ide?install=vscode)

   - **pip (Python):**
     ```sh
     pip install platformio
     ```

   - **Linux package managers:**  
     Some distributions provide PlatformIO or the `platformio` package in their official or community repositories. Try the command for your system:
     - **Debian/Ubuntu (apt):**
       ```sh
       sudo apt update
       sudo apt install platformio
       ```
     - **Fedora (dnf):**
       ```sh
       sudo dnf install platformio
       ```
     - **CentOS/RHEL (yum):**
       ```sh
       sudo yum install platformio
       ```
     - **Arch Linux/Manjaro (pacman):**
       ```sh
       sudo pacman -S platformio
       ```

   > If your package manager does not have PlatformIO, use the `pip` or VSCode extension options above.

2. **Clone this repository:**
   ```sh
   git clone https://github.com/ckoparir/ThermHome.git
   cd ThermHome
   ```

## Building & Uploading

Each subfolder (`wifi-client/` and `wifi-server/`) contains a PlatformIO project.

### Example (for the client):
```sh
cd wifi-client
platformio run                  # Build the project
platformio run --target upload  # Upload firmware to the connected ESP8266
```

Repeat similarly for `wifi-server/`:

```sh
cd wifi-server
platformio run
platformio run --target upload
```

#### Or, use VSCode:
- Open the desired subproject folder in VSCode.
- Use the PlatformIO sidebar to build or upload the firmware.

## Usage

1. **Connect your ESP8266 board via USB.**
2. **Build and upload** the firmware (see above).
3. **Configure WiFi and other settings** as required in the code or via serial monitor.
4. **Use the Android app** to interact with the device (see `/android-app` if available).

## Contributing

Pull requests are welcome! Please open an issue to discuss your ideas or for bug reports.

## License

This project is licensed under the terms of the [MIT License](LICENSE).
