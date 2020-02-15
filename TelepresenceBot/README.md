# Telepresence Bot (tpbot)
This folder contains the code for our amazing telepresence bot.
The project includes the following units:
* **Remote control unit**: an Android sender app with the remote control UI which will send movement commands to a receiver Android app, installed in the bot device.
* **HW-SW bridge unit**: a serial channel between the receiver Android app and the movement unit. It will be included in the receiver App, once finished.
* **Movement unit**: Arduino-controlled motors connected to a frame holding the Adroid device

## Using the Server Unit
Grab Node and install the required modules with `npm install`.

Open `server-unit` directory in command line and run `node server.js`

## Movement unit development
1. Download the official Arduino IDE from https://www.arduino.cc/en/Main/Software
2. Install the official library for the Adafruit Motor shield V1 https://github.com/adafruit/Adafruit-Motor-Shield-library
3. Load the sketch in `movement-unit`, compile and upload!

## Hardware
Both the Arduino and the motors are powered via USB from an external battery pack:
* a USB `Type-A` to `Type-B` cable connects the battery pack to the Arduino
* the motors use external power provided through a custom cable from USB `Type-A` to the 2 power wires. This connects the battery pack to the External Power input connector of the Motor Shield.

**Important:** remove the VIM jumper in the Motor Shield to use the external power instead of the one coming from Arduino.


![](https://github.com/novoda/spikes/raw/tpbot-feature-branch/TelepresenceBot/schematics/schematic.png)
In order to edit the schematics you need Fritzing (http://fritzing.org/) with the Adafruit Fritzing Library (https://github.com/adafruit/Fritzing-Library).
