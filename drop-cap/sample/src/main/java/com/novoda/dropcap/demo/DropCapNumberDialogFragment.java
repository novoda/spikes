package com.novoda.dropcap.demo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.novoda.drop_cap.R;

public class DropCapNumberDialogFragment extends DialogFragment {

    private OnDropCapNumberChangeListener onDropCapNumberChangeListener;
    private Button positiveButton;
    private Button negativeButton;
    private NumberPicker numberPicker;
    private int previousNumberOfDropCaps;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getResources().getString(R.string.text_size_title));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drop_cap_number_dialog, container);
        positiveButton = (Button) view.findViewById(R.id.drop_cap_number_button_positive);
        negativeButton = (Button) view.findViewById(R.id.drop_cap_number_button_negative);
        numberPicker = (NumberPicker) view.findViewById(R.id.drop_cap_number_picker);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numberPicker.setMinValue(getResources().getInteger(R.integer.drop_cap_number_picker_min));
        numberPicker.setMaxValue(getResources().getInteger(R.integer.drop_cap_number_picker_max));
        numberPicker.setValue(previousNumberOfDropCaps);

        positiveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newNumberOfDropCaps = numberPicker.getValue();
                        onDropCapNumberChangeListener.onDropCapNumberChanged(newNumberOfDropCaps);
                        dismiss();
                    }
                }
        );

        negativeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );
    }

    public void setDropCapNumberChangeListener(OnDropCapNumberChangeListener onDropCapNumberChangeListener) {
        this.onDropCapNumberChangeListener = onDropCapNumberChangeListener;
    }

    public void setPreviousNumberOfDropCaps(int previousNumberOfDropCaps) {
        this.previousNumberOfDropCaps = previousNumberOfDropCaps;
    }

}
