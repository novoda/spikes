package com.novoda.dropcap.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;

import com.novoda.drop_cap.R;

public class TypefaceDialogDisplayer {

    private final FragmentManager fragmentManager;
    private final Resources resources;

    private final OnTypefaceChangeListener onTypefaceChangeListener;

    public TypefaceDialogDisplayer(FragmentManager fragmentManager, Resources resources, OnTypefaceChangeListener onTypefaceChangeListener) {
        this.fragmentManager = fragmentManager;
        this.resources = resources;
        this.onTypefaceChangeListener = onTypefaceChangeListener;
    }

    public void showTypefaceDialog() {
        String textSizeFragmentTag = resources.getString(R.string.fragment_tag_typeface);
        TypefaceDialogFragment typefaceFragment = (TypefaceDialogFragment) fragmentManager.findFragmentByTag(textSizeFragmentTag);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (typefaceFragment == null) {
            typefaceFragment = new TypefaceDialogFragment();
            typefaceFragment.setTextSizeChangeListener(onTypefaceChangeListener);
        } else {
            transaction.remove(typefaceFragment);
        }

        typefaceFragment.show(transaction, textSizeFragmentTag);
    }

}
