package com.novoda.frankboylan.meetingseating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.novoda.frankboylan.meetingseating.rooms.heatmap.adv.repo.AdvHeatmapActivity;
import com.novoda.frankboylan.meetingseating.seats.SeatActivity;
import com.novoda.frankboylan.meetingseating.settings.SettingsActivity;

public class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        Intent intent = new Intent();
        switch (position) {
            case 0: // Seat list
                intent = new Intent(context, SeatActivity.class);
                break;
            case 1: // Heatmap
                intent = new Intent(context, AdvHeatmapActivity.class);
                break;
            case 2: // Settings
                intent = new Intent(context, SettingsActivity.class);
                break;
            case 3: // Logout
                intent = new Intent(context, LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                break;
            default: // Log a missing item
        }
        ((Activity) context).finish(); // Temp solution to fix back pressed security issue
        context.startActivity(intent);
    }
}
