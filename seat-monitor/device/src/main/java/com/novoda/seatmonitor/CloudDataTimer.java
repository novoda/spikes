package com.novoda.seatmonitor;

import android.os.CountDownTimer;

import com.novoda.loadgauge.CloudLoadGauges;

import java.util.concurrent.TimeUnit;

class CloudDataTimer extends CountDownTimer {

    private final String location;
    private final CloudIotCoreCommunicator communicator;
    private final RestartTimer restartTimer;
    private final CloudLoadGauges loadGauges;

    CloudDataTimer(String location, CloudIotCoreCommunicator communicator, CloudLoadGauges loadGauges, RestartTimer restartTimer) {
        super(TimeUnit.DAYS.toMillis(1), TimeUnit.SECONDS.toMillis(5));
        this.location = location;
        this.communicator = communicator;
        this.restartTimer = restartTimer;
        this.loadGauges = loadGauges;
    }

    @Override
    public void onTick(long l) {
        String msg = "{"
                + "\"location\" : \"" + location + "\", "
                + "\"totalLoadGauges\" : " + loadGauges.total() + ", "
                + "\"dataLoadGauges\" : " + loadGauges.asJsonArray() + ""
                + "}";
        communicator.publishMessage(msg);
    }

    @Override
    public void onFinish() {
        restartTimer.onFinished();
    }

    interface RestartTimer {
        void onFinished();
    }
}
