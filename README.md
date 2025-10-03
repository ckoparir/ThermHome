# ThermHome

ESP8266 Wifi IoT Project with Android Application

## Table of Contents
- [About](#about)
- [Project Structure](#project-structure)
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

## Requirements

- [PlatformIO](https://platformio.org/) (recommended via VSCode extension)
- Python 3.x
- Supported ESP8266 board (e.g., NodeMCU, Wemos D1 mini)

## Installation

1. **Install PlatformIO:**
   - [VSCode Extension Guide](https://platformio.org/install/ide?install=vscode)
   - Or, install via pip:
     ```sh
     pip install platformio
     ```

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
