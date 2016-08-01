package com.novoda.dropcap.demo;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

class SimpleSpinnerItemSelectedListener implements Spinner.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // no op.
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // no op.
    }

}
