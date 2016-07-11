package com.mecharyry.dropcap.demo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.mecharyry.drop_cap.R;

public class TextColorDialogFragment extends DialogFragment {

    private OnTextColorChangeListener onTextColorChangeListener;
    private Button positiveButton;
    private Button negativeButton;
    private ColorPicker colorPicker;
    private OpacityBar opacityBar;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    private int previousTextColor;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getResources().getString(R.string.text_size_title));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_color_dialog, container);
        positiveButton = (Button) view.findViewById(R.id.text_color_button_positive);
        negativeButton = (Button) view.findViewById(R.id.text_color_button_negative);
        colorPicker = (ColorPicker) view.findViewById(R.id.text_color_color_picker);
        opacityBar = (OpacityBar) view.findViewById(R.id.text_color_opacity_bar);
        saturationBar = (SaturationBar) view.findViewById(R.id.text_color_saturation_bar);
        valueBar = (ValueBar) view.findViewById(R.id.text_color_value_picker);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colorPicker.addOpacityBar(opacityBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);
        colorPicker.setColor(previousTextColor);

        positiveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newTextColor = colorPicker.getColor();
                        onTextColorChangeListener.onColorChanged(newTextColor);
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

    public void setTextColorChangeListener(OnTextColorChangeListener onTextColorChangeListener) {
        this.onTextColorChangeListener = onTextColorChangeListener;
    }

    public void setPreviousTextColor(int previousTextColor) {
        this.previousTextColor = previousTextColor;
    }

}
