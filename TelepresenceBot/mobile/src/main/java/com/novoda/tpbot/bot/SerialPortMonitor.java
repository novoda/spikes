package com.novoda.tpbot.bot;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.novoda.notils.exception.DeveloperError;
import com.novoda.notils.logger.simple.Log;

import java.io.UnsupportedEncodingException;

class SerialPortMonitor {

    private final UsbManager usbManager;
    private final SerialPortCreator serialPortCreator;

    private UsbSerialDevice serialPort;
    private UsbDeviceConnection deviceConnection;

    SerialPortMonitor(UsbManager usbManager, SerialPortCreator serialPortCreator) {
        this.usbManager = usbManager;
        this.serialPortCreator = serialPortCreator;
    }

    boolean tryToMonitorSerialPortFor(UsbDevice usbDevice, DataReceiver dataReceiver) {
        deviceConnection = usbManager.openDevice(usbDevice);
        serialPort = serialPortCreator.create(usbDevice, deviceConnection);

        if (deviceConnection == null || serialPort == null) {
            stopMonitoring();
            return false;
        }

        if (serialPort.open()) {
            serialPort.setBaudRate(9600);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            serialPort.read(new UsbReadCallbackStringForwarder(dataReceiver));
            return true;
        } else {
            stopMonitoring();
            return false;
        }

    }

    boolean tryToSendCommand(String command) {
        if (serialPort == null) {
            Log.d(getClass().getSimpleName(), "Not connected to SerialPort, unable to send command: " + command);
            return false;
        }

        serialPort.write(command.getBytes());
        return true;
    }

    void stopMonitoring() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }

        if (deviceConnection != null) {
            deviceConnection.close();
            deviceConnection = null;
        }
    }

    private class UsbReadCallbackStringForwarder implements UsbSerialInterface.UsbReadCallback {

        private final DataReceiver dataReceiver;

        private UsbReadCallbackStringForwarder(DataReceiver dataReceiver) {
            this.dataReceiver = dataReceiver;
        }

        @Override
        public void onReceivedData(byte[] bytes) {
            String data;
            try {
                data = new String(bytes, "UTF-8");
                dataReceiver.onReceive(data);
            } catch (UnsupportedEncodingException e) {
                throw new DeveloperError("Error receiving data from USB serial.");
            }
        }

    }

    interface DataReceiver {

        void onReceive(String data);
    }

}
