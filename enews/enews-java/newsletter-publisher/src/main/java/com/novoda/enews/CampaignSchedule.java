package com.novoda.enews;

import java.time.LocalDateTime;

class CampaignSchedule {
    private final LocalDateTime scheduleDateTime;
    private final boolean scheduleAsTimezoneLocal;

    public CampaignSchedule(LocalDateTime scheduleDateTime, boolean scheduleAsTimezoneLocal) {
        this.scheduleDateTime = scheduleDateTime;
        this.scheduleAsTimezoneLocal = scheduleAsTimezoneLocal;
    }

    public LocalDateTime getScheduleDateTime() {
        return scheduleDateTime;
    }

    public boolean isScheduleAsTimezoneLocal() {
        return scheduleAsTimezoneLocal;
    }
}
