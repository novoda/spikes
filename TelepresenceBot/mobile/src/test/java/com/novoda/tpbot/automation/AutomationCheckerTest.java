package com.novoda.tpbot.automation;

import com.novoda.support.ColonStringSplitter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class AutomationCheckerTest {

    private static final String SAMPLE_SERVICE_PACKAGE = "com.sample";
    private static final String EXAMPLE_SERVICE_PACKAGE = "com.example";
    private static final String APP_SERVICE_PACKAGE = "com.novoda.tpbot";
    private static final String RETRIEVED_SETTINGS = "com.sample:com.example:com.novoda.tpbot";

    private static final String[] CONTAINS_APP_SERVICE = new String[]{
            SAMPLE_SERVICE_PACKAGE,
            EXAMPLE_SERVICE_PACKAGE,
            APP_SERVICE_PACKAGE
    };

    private static final String[] MISSING_APP_SERVICE = new String[]{
            SAMPLE_SERVICE_PACKAGE,
            EXAMPLE_SERVICE_PACKAGE
    };

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AndroidAccessibilitySettingsRetriever settingsRetriever;

    @Mock
    ColonStringSplitter colonStringSplitter;

    private AutomationChecker automationChecker;

    @Before
    public void setUp() {
        automationChecker = new AutomationChecker(settingsRetriever, colonStringSplitter, APP_SERVICE_PACKAGE);
        given(colonStringSplitter.split(anyString())).willReturn(MISSING_APP_SERVICE);
    }

    @Test
    public void givenCanRetrieveAccessibilityServices_whenChecking_thenCallsColonStringSplitterWithRetrievedSettings() {
        given(settingsRetriever.retrieveEnabledAccessibilityServices()).willReturn(RETRIEVED_SETTINGS);

        automationChecker.isHangoutJoinerAutomationServiceEnabled();

        verify(colonStringSplitter).split(eq(RETRIEVED_SETTINGS));
    }

    @Test
    public void givenAccessibilityServicesContainsAppService_whenChecking_thenReturnsTrue() {
        given(colonStringSplitter.split(anyString())).willReturn(CONTAINS_APP_SERVICE);

        boolean serviceEnabled = automationChecker.isHangoutJoinerAutomationServiceEnabled();

        assertThat(serviceEnabled).isTrue();
    }

    @Test
    public void givenAccessibilityServicesMissingAppService_whenChecking_thenReturnsFalse() {
        given(colonStringSplitter.split(anyString())).willReturn(MISSING_APP_SERVICE);

        boolean serviceEnabled = automationChecker.isHangoutJoinerAutomationServiceEnabled();

        assertThat(serviceEnabled).isFalse();
    }
}
