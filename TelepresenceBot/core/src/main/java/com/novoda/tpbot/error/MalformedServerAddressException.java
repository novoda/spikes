package com.novoda.tpbot.error;

import java.io.IOException;

public class MalformedServerAddressException extends IOException {

    private static final String MALFORMED_ADDRESS_MESSAGE = "Address should be in the format `http://[ip_address]:[port_number]`";

    public MalformedServerAddressException(Throwable throwable) {
        super(MALFORMED_ADDRESS_MESSAGE, throwable);
    }
}
