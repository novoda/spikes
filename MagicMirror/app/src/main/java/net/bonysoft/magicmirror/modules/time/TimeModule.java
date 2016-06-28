package net.bonysoft.magicmirror.modules.time;

import android.widget.TextView;

import net.bonysoft.magicmirror.modules.DashboardModule;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class TimeModule implements DashboardModule {

    private static final DateTimeFormatter HOUR_MINUTES_FORMATTER = new DateTimeFormatterBuilder()
            .appendHourOfDay(2)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .toFormatter();

    private static final DateTimeFormatter DAY_DAYMONTH_MONTH = new DateTimeFormatterBuilder()
            .appendDayOfWeekShortText()
            .appendLiteral(" ")
            .appendDayOfMonth(1)
            .appendLiteral(" ")
            .appendMonthOfYearShortText()
            .toFormatter();

    private final TextView timeLabel;
    private final TextView dateLabel;

    public TimeModule(TextView timeLabel, TextView dateLabel) {
        this.timeLabel = timeLabel;
        this.dateLabel = dateLabel;
    }

    @Override
    public void update() {
        DateTime dateTime = DateTime.now();
        String formattedTime = HOUR_MINUTES_FORMATTER.print(dateTime);
        timeLabel.setText(formattedTime);

        String formattedDate = DAY_DAYMONTH_MONTH.print(dateTime);
        dateLabel.setText(formattedDate);
    }

    @Override
    public void stop() {
        // no-op
    }

}
