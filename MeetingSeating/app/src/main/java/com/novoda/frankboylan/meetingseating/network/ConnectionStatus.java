package com.novoda.frankboylan.meetingseating.network;

import java.io.IOException;

/**
 * System Ping Google to check if connected to internet
 */
public class ConnectionStatus {
    public static boolean hasActiveInternetConnection() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
