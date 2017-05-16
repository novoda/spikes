package com.novoda.dropcap.demo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;

import com.novoda.drop_cap.R;

public class DropCapNumberDialogDisplayer {

    private final FragmentManager fragmentManager;
    private final Resources resources;

    private final OnDropCapNumberChangeListener numberChangeListener;

    public DropCapNumberDialogDisplayer(FragmentManager fragmentManager, Resources resources, OnDropCapNumberChangeListener numberChangeListener) {
        this.fragmentManager = fragmentManager;
        this.resources = resources;
        this.numberChangeListener = numberChangeListener;
    }

    public void showDropCapNumberDialog(int previousDropCapNumber) {
        String dropCapNumberFragmentTag = resources.getString(R.string.fragment_tag_drop_cap_number);
        DropCapNumberDialogFragment dropCapNumberFragment = (DropCapNumberDialogFragment) fragmentManager.findFragmentByTag(dropCapNumberFragmentTag);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (dropCapNumberFragment == null) {
            dropCapNumberFragment = new DropCapNumberDialogFragment();
            dropCapNumberFragment.setDropCapNumberChangeListener(numberChangeListener);
            dropCapNumberFragment.setPreviousNumberOfDropCaps(previousDropCapNumber);
        } else {
            transaction.remove(dropCapNumberFragment);
        }

        dropCapNumberFragment.show(transaction, dropCapNumberFragmentTag);
    }

}
