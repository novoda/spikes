package com.novoda.dropcap.demo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.novoda.drop_cap.R;

public class TypefaceDialogFragment extends DialogFragment {

    private OnTypefaceChangeListener onTypefaceChangeListener;
    private Button positiveButton;
    private Button negativeButton;
    private Spinner typefacePicker;
    private int previousTypefacePosition = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(getResources().getString(R.string.text_size_title));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_typeface_dialog, container);
        positiveButton = (Button) view.findViewById(R.id.text_size_button_positive);
        negativeButton = (Button) view.findViewById(R.id.text_size_button_negative);
        typefacePicker = (Spinner) view.findViewById(R.id.typeface_picker);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<FontType> fonts = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                FontType.values()
        );
        typefacePicker.setAdapter(fonts);
        typefacePicker.setSelection(previousTypefacePosition);

        positiveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fontName = typefacePicker.getSelectedItem().toString();
                        FontType fontType = FontType.valueOf(fontName);
                        String fontPath = getResources().getString(fontType.getAssetUrl());
                        onTypefaceChangeListener.onTypefaceChanged(fontPath);
                        previousTypefacePosition = typefacePicker.getSelectedItemPosition();
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

    public void setTextSizeChangeListener(OnTypefaceChangeListener onTypefaceChangeListener) {
        this.onTypefaceChangeListener = onTypefaceChangeListener;
    }

}
