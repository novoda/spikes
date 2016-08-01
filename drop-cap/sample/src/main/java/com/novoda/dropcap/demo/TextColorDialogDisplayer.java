package com.novoda.dropcap.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;

import com.novoda.drop_cap.R;

class TextColorDialogDisplayer {

    private final FragmentManager fragmentManager;
    private final Resources resources;

    private final OnTextColorChangeListener textColorChangedListener;

    public TextColorDialogDisplayer(FragmentManager fragmentManager, Resources resources, OnTextColorChangeListener onTextColorChangeListener) {
        this.fragmentManager = fragmentManager;
        this.resources = resources;
        this.textColorChangedListener = onTextColorChangeListener;
    }

    public void showTextColorDialog(int previousTextColor) {
        String textColorFragmentTag = resources.getString(R.string.fragment_tag_text_color);
        TextColorDialogFragment textColorFragment = (TextColorDialogFragment) fragmentManager.findFragmentByTag(textColorFragmentTag);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (textColorFragment == null) {
            textColorFragment = new TextColorDialogFragment();
            textColorFragment.setTextColorChangeListener(textColorChangedListener);
            textColorFragment.setPreviousTextColor(previousTextColor);
        } else {
            transaction.remove(textColorFragment);
        }

        textColorFragment.show(transaction, textColorFragmentTag);
    }

}
