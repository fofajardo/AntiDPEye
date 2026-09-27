# AntiDPEye for Android

<div style="text-align: center;">
  <img alt="AntiDPEye logo" src=".github/images/logo.svg" width="100%" height="200px">
</div>

An Android application that runs a local VPN service to help bypass DPI (Deep Packet Inspection) and censorship.

AntiDPEye runs the [ByeDPI](https://github.com/hufrea/byedpi) SOCKS5 proxy and redirects your device's traffic through it. This is a fork of the [ByeDPI for Android](https://github.com/dovecoteescapee/ByeDPIAndroid) project.

## Installation

[<img src="https://raw.githubusercontent.com/rubenpgrady/get-it-on-github/refs/heads/main/get-it-on-github.png" alt="Get it on GitHub" height="96" />](https://github.com/fofajardo/AntiDPEye/releases)

### Using Obtainium

1. Install [Obtainium](https://github.com/ImranR98/Obtainium/blob/main/README.md#installation).
2. Add AntiDPEye using this URL:
   `https://github.com/fofajardo/AntiDPEye`

## Settings

Some blocks may require you to adjust the application settings. For details about the available options, see the [ByeDPI documentation](https://github.com/hufrea/byedpi/blob/main/README.md).

## FAQ

### Does AntiDPEye require root access?

No. All features work without root access.

### Is AntiDPEye a VPN?

Not in the traditional sense. AntiDPEye uses Android's VPN mode to redirect traffic through a local proxy, but it does not connect to a remote VPN server.

AntiDPEye does not encrypt your traffic or hide your IP address.

### How can I use AntiDPEye with AdGuard?

1. Run AntiDPEye in proxy mode.
2. Add AntiDPEye to AdGuard's exceptions on the **App management** tab.
3. In AdGuard's settings, configure the proxy as follows:

   ```plaintext
   Proxy type: SOCKS5
   Proxy host: 127.0.0.1
   Proxy port: 1080
   ```

   Port `1080` is used by default.

### What data does AntiDPEye collect?

None. AntiDPEye does not send data to any remote server. All traffic is processed locally on your device.

### Are there versions for other platforms?

See the list of [similar projects](https://github.com/ValdikSS/GoodbyeDPI/blob/master/README.md#similar-projects).

### What is DPI?

Deep Packet Inspection (DPI) is a technology used to analyze and filter network traffic. Internet service providers and government agencies may use it to block access to websites and services.

## Dependencies

- [ByeDPI](https://github.com/hufrea/byedpi)
- [hev-socks5-tunnel](https://github.com/heiher/hev-socks5-tunnel)

## Building

To build AntiDPEye, you will need:

1. JDK 17 or later
2. Android SDK
3. Android NDK
4. CMake 4.1.2 or later

Then follow these steps:

1. Clone the repository and its submodules:

   ```bash
   git clone --recurse-submodules
   ```

2. Run the build script from the repository root:

   ```bash
   ./gradlew assembleRelease
   ```

3. The generated APK will be located at:

   `app/build/outputs/apk/release/`
