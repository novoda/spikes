package com.novoda.tpbot.bot;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.novoda.notils.logger.simple.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class SerialPortMonitorTest {

    private static final String ANY_DATA = "any data";
    private static final String ANY_COMMAND = "w";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UsbManager usbManager;
    @Mock
    private SerialPortMonitor.DataReceiver dataReceiver;
    @Mock
    private SerialPortCreator serialPortCreator;

    @Mock
    private UsbDevice usbDevice;
    @Mock
    private UsbSerialDevice usbSerialPort;
    @Mock
    private UsbDeviceConnection usbDeviceConnection;

    private SerialPortMonitor serialPortMonitor;

    @Before
    public void setUp() {
        Log.setShowLogs(false);
        serialPortMonitor = new SerialPortMonitor(usbManager, serialPortCreator);
    }

    @Test
    public void givenUnableToOpenDevice_whenTryingToMonitorSerialPort_thenReturnsFalse() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(null);

        boolean monitoring = serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        assertThat(monitoring).isFalse();
    }

    @Test
    public void givenAbleToOpenDevice_butUnableToCreateSerialPort_whenTryingToMonitorSerialPort_thenReturnsFalse() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(null);

        boolean monitoring = serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        assertThat(monitoring).isFalse();
    }

    @Test
    public void givenAbleToOpenDevice_butUnableToCreateSerialPort_whenTryingToMonitorSerialPort_thenClosesDeviceConnection() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(null);

        serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        verify(usbDeviceConnection).close();
    }

    @Test
    public void givenUnableToOpenSerialPort_whenTryingToMonitorSerialPort_thenReturnsFalse() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(usbSerialPort);
        given(usbSerialPort.open()).willReturn(false);

        boolean monitoring = serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        assertThat(monitoring).isFalse();
    }

    @Test
    public void givenUnableToOpenSerialPort_whenTryingToMonitorSerialPort_thenClosesDeviceConnection() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(usbSerialPort);
        given(usbSerialPort.open()).willReturn(false);

        serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        verify(usbDeviceConnection).close();
    }

    @Test
    public void givenAbleToOpenSerialPort_whenTryingToMonitorSerialPort_thenReturnsTrue() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(usbSerialPort);
        given(usbSerialPort.open()).willReturn(true);

        boolean monitoring = serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        assertThat(monitoring).isTrue();
    }

    @Test
    public void givenAbleToOpenSerialPort_whenTryingToMonitorSerialPort_andDataIsReceived_thenDataIsRecieved() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(usbSerialPort);
        given(usbSerialPort.open()).willReturn(true);

        serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        ArgumentCaptor<UsbSerialInterface.UsbReadCallback> argumentCaptor = ArgumentCaptor.forClass(UsbSerialInterface.UsbReadCallback.class);
        verify(usbSerialPort).read(argumentCaptor.capture());
        argumentCaptor.getValue().onReceivedData(ANY_DATA.getBytes());
        verify(dataReceiver).onReceive(ANY_DATA);
    }

    @Test
    public void givenUnableToOpenSerialPort_whenTryingToMonitorSerialPort_thenClosesSerialPort() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(usbSerialPort);
        given(usbSerialPort.open()).willReturn(false);

        serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);

        verify(usbSerialPort).close();
    }

    @Test
    public void givenSerialPortIsNotConnected_whenTryingToSendCommand_thenReturnsFalse() {

        boolean commandSent = serialPortMonitor.tryToSendCommand(ANY_COMMAND);

        assertThat(commandSent).isFalse();
    }

    @Test
    public void givenSerialPortIsConnected_whenTryingToSendCommand_thenReturnsFalse() {
        givenSerialPortIsConnected();

        boolean commandSent = serialPortMonitor.tryToSendCommand(ANY_COMMAND);

        assertThat(commandSent).isTrue();
    }

    @Test
    public void givenSerialPortIsConnected_whenTryingToSendCommand_thenWritesCommandToSerialPort() {
        givenSerialPortIsConnected();

        serialPortMonitor.tryToSendCommand(ANY_COMMAND);

        verify(usbSerialPort).write(ANY_COMMAND.getBytes());
    }

    @Test
    public void givenSerialPortIsConnected_whenStopping_thenClosesSerialPort() {
        givenSerialPortIsConnected();

        serialPortMonitor.stopMonitoring();

        verify(usbSerialPort).close();
    }

    @Test
    public void givenSerialPortIsConnected_whenStopping_thenClosesDeviceConnection() {
        givenSerialPortIsConnected();

        serialPortMonitor.stopMonitoring();

        verify(usbDeviceConnection).close();
    }

    private void givenSerialPortIsConnected() {
        given(usbManager.openDevice(any(UsbDevice.class))).willReturn(usbDeviceConnection);
        given(serialPortCreator.create(any(UsbDevice.class), any(UsbDeviceConnection.class))).willReturn(usbSerialPort);
        given(usbSerialPort.open()).willReturn(true);

        serialPortMonitor.tryToMonitorSerialPortFor(usbDevice, dataReceiver);
    }
}
