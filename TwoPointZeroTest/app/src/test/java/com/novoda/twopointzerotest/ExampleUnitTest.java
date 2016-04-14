package com.novoda.twopointzerotest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Mock
    RelativeLayout foo;
    @Mock
    ViewGroup.LayoutParams fooe;
    @Mock
    LinearLayout foow;
    @Mock
    Resources fooq;
    @Mock
    Activity foo1;
    @Mock
    Context foo2;
    @Mock
    View foo3;
    @Mock
    ViewGroup foo4;
    @Mock
    GridLayout foo5;
    @Mock
    Settings.System foo6;
    @Mock
    AssetManager foo7;
    @Mock
    AccountManager foo8;
    @Mock
    Fragment foo9;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(foo.getAccessibilityClassName()).thenReturn("a11y");
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}
