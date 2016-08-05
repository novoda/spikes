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

public class TextSizeDialogFragment extends DialogFragment {

    private OnTextSizeChangeListener onTextSizeChangeListener;
    private Button positiveButton;
    private Button negativeButton;
    private NumberPicker numberPicker;
    private int previousTextSize;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getResources().getString(R.string.text_size_title));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_size_dialog, container);
        positiveButton = (Button) view.findViewById(R.id.text_size_button_positive);
        negativeButton = (Button) view.findViewById(R.id.text_size_button_negative);
        numberPicker = (NumberPicker) view.findViewById(R.id.text_size_number_picker);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numberPicker.setMinValue(getResources().getDimensionPixelSize(R.dimen.text_size_number_picker_min));
        numberPicker.setMaxValue(getResources().getDimensionPixelSize(R.dimen.text_size_number_picker_max));
        numberPicker.setValue(previousTextSize);

        positiveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newTextSize = numberPicker.getValue();
                        onTextSizeChangeListener.onSizeChanged(newTextSize);
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

    public void setTextSizeChangeListener(OnTextSizeChangeListener onTextSizeChangeListener) {
        this.onTextSizeChangeListener = onTextSizeChangeListener;
    }

    public void setPreviousTextSize(int previousTextSize) {
        this.previousTextSize = previousTextSize;
    }

}
