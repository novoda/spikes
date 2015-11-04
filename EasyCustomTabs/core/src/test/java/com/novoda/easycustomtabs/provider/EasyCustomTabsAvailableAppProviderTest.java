package com.novoda.easycustomtabs.provider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class EasyCustomTabsAvailableAppProviderTest {

    private static final String NON_EMPTY_PACKAGE = "non.empty.package";

    @Mock
    private BestPackageFinder mockBestPackageFinder;
    @Mock
    private EasyCustomTabsAvailableAppProvider.PackageFoundCallback mockPackageFoundCallback;

    private EasyCustomTabsAvailableAppProvider easyCustomTabsAvailableAppProvider;

    @Before
    public void setUp() {
        initMocks(this);

        easyCustomTabsAvailableAppProvider = new EasyCustomTabsAvailableAppProvider(createObservable(), mockBestPackageFinder);

    }

    private Observable<String> createObservable() {
        return Observable.<String>empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @Test
    public void findBestPackageDelegatesToPackageFinder() {
        easyCustomTabsAvailableAppProvider.findBestPackage(mockPackageFoundCallback);

        verify(mockBestPackageFinder).findBestPackage();
    }

    @Test
    public void packageIsFoundIfNotNullOrEmpty() {
        when(mockBestPackageFinder.findBestPackage()).thenReturn(NON_EMPTY_PACKAGE);

        easyCustomTabsAvailableAppProvider.findBestPackage(mockPackageFoundCallback);

        verify(mockPackageFoundCallback).onPackageFound(NON_EMPTY_PACKAGE);
    }

    @Test
    public void packageIsNotFoundIfNull() {
        easyCustomTabsAvailableAppProvider.findBestPackage(mockPackageFoundCallback);

        when(mockBestPackageFinder.findBestPackage()).thenReturn(null);

        verify(mockPackageFoundCallback).onPackageNotFound();
    }

    @Test
    public void packageNotFoundIfEmpty() {
        easyCustomTabsAvailableAppProvider.findBestPackage(mockPackageFoundCallback);

        when(mockBestPackageFinder.findBestPackage()).thenReturn("");

        verify(mockPackageFoundCallback).onPackageNotFound();
    }

}
