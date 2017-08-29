package com.novoda.tpbot.bot.usb;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.novoda.support.Optional;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class SupportedDeviceRetrieverTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    UsbManager usbManager;

    private SupportedDeviceRetriever supportedDeviceRetriever;

    @Before
    public void setUp() {
        supportedDeviceRetriever = new SupportedDeviceRetriever(usbManager);
    }

    @Test
    public void givenEmptyDeviceList_whenRetrievingFirstSupportedDevice_thenReturnsAbsent() {
        given(usbManager.getDeviceList()).willReturn(new HashMap<String, UsbDevice>());

        Optional<UsbDevice> usbDevice = supportedDeviceRetriever.retrieveFirstSupportedUsbDevice();

        assertThat(usbDevice.isPresent()).isFalse();
    }

    @Test
    public void givenDeviceList_withMultipleSupportedDevices_whenRetrievingFirstSupportedDevice_thenReturnsFirstSupportedDevice() {
        UsbDevice expectedUsbDevice = givenDeviceListWithMultipleSupportedDevices();

        Optional<UsbDevice> usbDevice = supportedDeviceRetriever.retrieveFirstSupportedUsbDevice();

        assertThat(usbDevice.get()).isEqualTo(expectedUsbDevice);
    }

    private UsbDevice givenDeviceListWithMultipleSupportedDevices() {
        UsbDevice usbDeviceOne = Mockito.mock(UsbDevice.class);
        given(usbDeviceOne.getVendorId()).willReturn(9025);
        UsbDevice usbDeviceTwo = Mockito.mock(UsbDevice.class);
        given(usbDeviceTwo.getVendorId()).willReturn(10755);

        HashMap<String, UsbDevice> usbDevices = new HashMap<>();
        usbDevices.put("one", usbDeviceOne);
        usbDevices.put("two", usbDeviceTwo);

        given(usbManager.getDeviceList()).willReturn(usbDevices);
        return usbDeviceOne;
    }

}
