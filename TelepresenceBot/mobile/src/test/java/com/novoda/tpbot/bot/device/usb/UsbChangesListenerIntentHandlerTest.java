package com.novoda.tpbot.bot.device.usb;

import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import com.novoda.notils.exception.DeveloperError;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class UsbChangesListenerIntentHandlerTest {

    private static final String ACTION_USB_PERMISSION = "com.novoda.tpbot.USB_PERMISSION";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Intent intent;
    @Mock
    Bundle bundle;
    @Mock
    UsbChangesListener usbChangesListener;

    @Before
    public void setUp() {
        given(intent.getExtras()).willReturn(bundle);
    }

    @Test
    public void givenPermissionAction_andPermissionIsGranted_whenHandlingIntent_thenGrantsPermission() {
        given(bundle.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)).willReturn(true);
        UsbChangesListenerIntentHandler.IntentHandler handler = UsbChangesListenerIntentHandler.get(ACTION_USB_PERMISSION);

        handler.handle(intent, usbChangesListener);

        verify(usbChangesListener).onPermissionGranted();
    }

    @Test
    public void givenPermissionAction_andPermissionIsDenied_whenHandlingIntent_thenDeniesPermission() {
        given(bundle.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)).willReturn(false);
        UsbChangesListenerIntentHandler.IntentHandler handler = UsbChangesListenerIntentHandler.get(ACTION_USB_PERMISSION);

        handler.handle(intent, usbChangesListener);

        verify(usbChangesListener).onPermissionDenied();
    }

    @Test
    public void givenDeviceAttachedAction_whenHandlingIntent_thenAttachesDevice() {
        UsbChangesListenerIntentHandler.IntentHandler handler = UsbChangesListenerIntentHandler.get(UsbManager.ACTION_USB_DEVICE_ATTACHED);

        handler.handle(intent, usbChangesListener);

        verify(usbChangesListener).onDeviceAttached();
    }

    @Test
    public void givenDeviceDetachedAction_whenHandlingIntent_thenDetachesDevice() {
        UsbChangesListenerIntentHandler.IntentHandler handler = UsbChangesListenerIntentHandler.get(UsbManager.ACTION_USB_DEVICE_DETACHED);

        handler.handle(intent, usbChangesListener);

        verify(usbChangesListener).onDeviceDetached();
    }

    @Test(expected = DeveloperError.class)
    public void whenRetrievingHandler_withUnknownAction_thenThrowsDeveloperError() {
        UsbChangesListenerIntentHandler.get("unknown action");
    }

}
