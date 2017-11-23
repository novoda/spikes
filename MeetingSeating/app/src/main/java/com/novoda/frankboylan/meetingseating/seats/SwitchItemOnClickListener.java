package com.novoda.frankboylan.meetingseating.seats;

import android.view.View;
import android.widget.Switch;

class SwitchItemOnClickListener implements Switch.OnClickListener {
    private Switch button;
    private SeatEnabler enabler;

    SwitchItemOnClickListener(Switch newSwitch, SeatEnabler enabler) {
        this.button = newSwitch;
        this.enabler = enabler;
    }

    @Override
    public void onClick(View v) {
        if (button.getTag().equals("Room")) {
            if (button.isChecked()) {
                enabler.checkSeatsWithMatchingId(button.getId());
                return;
            }
            enabler.uncheckSeatsWithMatchingId(button.getId());
        }
    }
}
