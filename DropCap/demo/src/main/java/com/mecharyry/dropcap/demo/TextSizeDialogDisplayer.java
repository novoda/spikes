package com.mecharyry.dropcap.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;

import com.mecharyry.drop_cap.R;

public class TextSizeDialogDisplayer {

    private final FragmentManager fragmentManager;
    private final Resources resources;

    private final OnTextSizeChangeListener textSizeChangedListener;

    public TextSizeDialogDisplayer(FragmentManager fragmentManager, Resources resources, OnTextSizeChangeListener onTextSizeChangeListener) {
        this.fragmentManager = fragmentManager;
        this.resources = resources;
        this.textSizeChangedListener = onTextSizeChangeListener;
    }

    public void showTextSizeDialog(float previousTextSize) {
        String textSizeFragmentTag = resources.getString(R.string.fragment_tag_text_size);
        TextSizeDialogFragment textSizeFragment = (TextSizeDialogFragment) fragmentManager.findFragmentByTag(textSizeFragmentTag);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (textSizeFragment == null) {
            textSizeFragment = new TextSizeDialogFragment();
            textSizeFragment.setTextSizeChangeListener(textSizeChangedListener);
            textSizeFragment.setPreviousTextSize((int) previousTextSize);
        } else {
            transaction.remove(textSizeFragment);
        }

        textSizeFragment.show(transaction, textSizeFragmentTag);
    }

}
