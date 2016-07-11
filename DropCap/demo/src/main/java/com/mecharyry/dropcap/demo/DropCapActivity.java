package com.mecharyry.dropcap.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.mecharyry.drop_cap.R;
import com.mecharyry.dropcap.DropCapView;

public class DropCapActivity extends Activity {

    private DropCapView dropCapView;
    private TextSizeDialogDisplayer copyTextSizeDialogDisplayer;
    private TextSizeDialogDisplayer dropCapSizeDialogDisplayer;
    private TextColorDialogDisplayer dropCapTextColorDialogDisplayer;
    private TextColorDialogDisplayer copyTextColorDialogDisplayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_cap);
        dropCapView = (DropCapView) findViewById(R.id.view_drop_cap);

        String dropCapText = getResources().getString(R.string.drop_cap_dummy_text);
        dropCapView.setText(dropCapText);

        createTextSizeDialogDisplayers();
        createTextColorDialogDisplayers();
        createFontTypeSpinner();
    }

    private void createTextSizeDialogDisplayers() {
        dropCapSizeDialogDisplayer = new TextSizeDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onDropCapTextSizeChanged
        );

        copyTextSizeDialogDisplayer = new TextSizeDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onCopyTextSizeChanged
        );

        Button dropCapTextSizeButton = (Button) findViewById(R.id.drop_cap_size);
        dropCapTextSizeButton.setOnClickListener(onClickDisplayDropCapTextSizeDialog);

        Button copyTextSizeButton = (Button) findViewById(R.id.copy_size);
        copyTextSizeButton.setOnClickListener(onClickDisplayCopyTextSizeDialog);

    }

    private final OnTextSizeChangeListener onDropCapTextSizeChanged = new OnTextSizeChangeListener() {
        @Override
        public void onSizeChanged(int newTextSize) {
            dropCapView.setDropCapTextSize(newTextSize);
        }
    };

    private final OnTextSizeChangeListener onCopyTextSizeChanged = new OnTextSizeChangeListener() {
        @Override
        public void onSizeChanged(int newTextSize) {
            dropCapView.setCopyTextSize(newTextSize);
        }
    };

    private final View.OnClickListener onClickDisplayDropCapTextSizeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dropCapSizeDialogDisplayer.showTextSizeDialog(dropCapView.getDropCapTextSize());
        }
    };

    private final View.OnClickListener onClickDisplayCopyTextSizeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            copyTextSizeDialogDisplayer.showTextSizeDialog(dropCapView.getCopyTextSize());
        }
    };

    private void createTextColorDialogDisplayers() {
        dropCapTextColorDialogDisplayer = new TextColorDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onDropCapTextColorChanged
        );

        copyTextColorDialogDisplayer = new TextColorDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onCopyTextColorChanged
        );

        Button dropCapTextColorButton = (Button) findViewById(R.id.drop_cap_color);
        dropCapTextColorButton.setOnClickListener(onClickDisplayDropCapTextColorDialog);

        Button copyTextColorButton = (Button) findViewById(R.id.copy_color);
        copyTextColorButton.setOnClickListener(onClickDisplayCopyTextColorDialog);

    }

    private final OnTextColorChangeListener onDropCapTextColorChanged = new OnTextColorChangeListener() {
        @Override
        public void onColorChanged(@ColorInt int color) {
            dropCapView.setDropCapTextColor(color);
        }
    };

    private final OnTextColorChangeListener onCopyTextColorChanged = new OnTextColorChangeListener() {
        @Override
        public void onColorChanged(@ColorInt int color) {
            dropCapView.setCopyTextColor(color);
        }
    };

    private final View.OnClickListener onClickDisplayDropCapTextColorDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dropCapTextColorDialogDisplayer.showTextColorDialog(dropCapView.getDropCapTextColor());
        }
    };

    private final View.OnClickListener onClickDisplayCopyTextColorDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            copyTextColorDialogDisplayer.showTextColorDialog(dropCapView.getCopyTextColor());
        }
    };

    private void createFontTypeSpinner() {
        Spinner dropCapFontSpinner = (Spinner) findViewById(R.id.drop_cap_font_spinner);
        Spinner copyFontSpinner = (Spinner) findViewById(R.id.copy_font_spinner);

        ArrayAdapter<FontType> fonts = new ArrayAdapter<FontType>(
                this,
                android.R.layout.simple_spinner_item,
                FontType.values()
        );
        dropCapFontSpinner.setAdapter(fonts);
        dropCapFontSpinner.setSelection(0);
        dropCapFontSpinner.setOnItemSelectedListener(dropCapSelectedFontListener);

        copyFontSpinner.setAdapter(fonts);
        copyFontSpinner.setSelection(0);
        copyFontSpinner.setOnItemSelectedListener(copySelectedFontListener);
    }

    private final SimpleSpinnerItemSelectedListener dropCapSelectedFontListener = new SimpleSpinnerItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String fontName = parent.getItemAtPosition(position).toString();
            FontType fontType = FontType.valueOf(fontName);
            String fontPath = getResources().getString(fontType.getAssetUrl());
            dropCapView.setDropCapFontType(fontPath);
        }

    };

    private final SimpleSpinnerItemSelectedListener copySelectedFontListener = new SimpleSpinnerItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String fontName = parent.getItemAtPosition(position).toString();
            FontType fontType = FontType.valueOf(fontName);
            String fontPath = getResources().getString(fontType.getAssetUrl());
            dropCapView.setCopyFontType(fontPath);
        }

    };

}
