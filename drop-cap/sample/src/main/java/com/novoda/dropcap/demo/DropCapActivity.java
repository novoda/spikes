package com.novoda.dropcap.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.novoda.drop_cap.R;
import com.novoda.dropcap.DropCapView;

public class DropCapActivity extends Activity {

    private DropCapView dropCapView;
    private TextSizeDialogDisplayer copyTextSizeDialogDisplayer;
    private TextSizeDialogDisplayer dropCapSizeDialogDisplayer;
    private TextColorDialogDisplayer dropCapTextColorDialogDisplayer;
    private TextColorDialogDisplayer copyTextColorDialogDisplayer;
    private TypefaceDialogDisplayer dropCapTypefaceDialogDisplayer;
    private TypefaceDialogDisplayer copyTypefaceDialogDisplayer;
    private DropCapNumberDialogDisplayer dropCapNumberDialogDisplayer;

    private int numberOfDropCaps = 1;
    private FontType dropCapFontType;
    private FontType copyFontType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_cap);
        dropCapView = (DropCapView) findViewById(R.id.view_drop_cap);

        createTextSizeDialogDisplayers();
        createTextColorDialogDisplayers();
        createTypefaceDialogDisplayers();
        createTextUpdater();
        createDropCapNumberUpdater();
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
            dropCapView.setDropCapTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
        }
    };

    private final OnTextSizeChangeListener onCopyTextSizeChanged = new OnTextSizeChangeListener() {
        @Override
        public void onSizeChanged(int newTextSize) {
            dropCapView.setCopyTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
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

    private void createTypefaceDialogDisplayers() {
        dropCapTypefaceDialogDisplayer = new TypefaceDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onDropCapTypefaceChanged
        );

        copyTypefaceDialogDisplayer = new TypefaceDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onCopyTypefaceChanged
        );

        Button dropCapTypefaceButton = (Button) findViewById(R.id.drop_cap_typeface);
        dropCapTypefaceButton.setOnClickListener(onClickDisplayDropCapTypefaceDialog);

        Button copyTypefaceButton = (Button) findViewById(R.id.copy_typeface);
        copyTypefaceButton.setOnClickListener(onClickDisplayCopyTypefaceDialog);
    }

    private final OnTypefaceChangeListener onDropCapTypefaceChanged = new OnTypefaceChangeListener() {
        @Override
        public void onTypefaceChanged(FontType newFontType) {
            String fontPath = getResources().getString(newFontType.getAssetUrl());
            dropCapView.setDropCapFontType(fontPath);
            dropCapFontType = newFontType;
        }
    };

    private final OnTypefaceChangeListener onCopyTypefaceChanged = new OnTypefaceChangeListener() {
        @Override
        public void onTypefaceChanged(FontType newFontType) {
            String fontPath = getResources().getString(newFontType.getAssetUrl());
            dropCapView.setCopyFontType(fontPath);
            copyFontType = newFontType;
        }
    };

    private final View.OnClickListener onClickDisplayDropCapTypefaceDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dropCapTypefaceDialogDisplayer.showTypefaceDialog(dropCapFontType);
        }
    };

    private final View.OnClickListener onClickDisplayCopyTypefaceDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            copyTypefaceDialogDisplayer.showTypefaceDialog(copyFontType);
        }
    };

    private void createDropCapNumberUpdater() {
        dropCapNumberDialogDisplayer = new DropCapNumberDialogDisplayer(
                getFragmentManager(),
                getResources(),
                onDropCapNumberChangeListener
        );

        Button numberOfDropCapsButton = (Button) findViewById(R.id.drop_cap_number);
        numberOfDropCapsButton.setOnClickListener(onClickDisplayNumberOfDropCapsDialog);
    }

    private final OnDropCapNumberChangeListener onDropCapNumberChangeListener = new OnDropCapNumberChangeListener() {
        @Override
        public void onDropCapNumberChanged(int newNumberOfDropCaps) {
            dropCapView.setNumberOfDropCaps(newNumberOfDropCaps);
            numberOfDropCaps = newNumberOfDropCaps;
        }
    };

    private final View.OnClickListener onClickDisplayNumberOfDropCapsDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dropCapNumberDialogDisplayer.showDropCapNumberDialog(numberOfDropCaps);
        }
    };

    private void createTextUpdater() {
        final EditText textUpdateEditText = (EditText) findViewById(R.id.drop_cap_edit_text);
        Button textUpdateButton = (Button) findViewById(R.id.edit_text_update);

        textUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedText = textUpdateEditText.getText().toString();
                dropCapView.setText(updatedText);
            }
        });
    }

}
