package com.novoda.tpbot.bot.video.calling;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class AutomationCheckerTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AccessibilityManager accessibilityManager;

    private AutomationChecker automationChecker;

    private static final List<AccessibilityServiceInfo> CONTAINS_APP_SERVICE = Arrays.<AccessibilityServiceInfo>asList(
            new TestAccessibilityServiceInfo("com.example.foo/Service"),
            new TestAccessibilityServiceInfo("com.google.talk/Service"),
            new TestAccessibilityServiceInfo("com.novoda.tpbot/.automation.HangoutJoinerAutomationService")
    );

    private static final List<AccessibilityServiceInfo> MISSING_APP_SERVICE = Arrays.<AccessibilityServiceInfo>asList(
            new TestAccessibilityServiceInfo("com.example.foo/Service"),
            new TestAccessibilityServiceInfo("com.google.talk/Service")
    );

    @Before
    public void setUp() {
        automationChecker = new AutomationChecker(accessibilityManager);
    }

    @Test
    public void givenAccessibilityServiceList_withAutomationService_whenChecking_thenReturnsTrue() {
        given(accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_VISUAL)).willReturn(CONTAINS_APP_SERVICE);

        boolean serviceEnabled = automationChecker.isHangoutJoinerAutomationServiceEnabled();

        assertThat(serviceEnabled).isTrue();
    }

    @Test
    public void givenAccessibilityServiceList_withoutAutomationService_whenChecking_thenReturnsFalse() {
        given(accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_VISUAL)).willReturn(MISSING_APP_SERVICE);

        boolean serviceEnabled = automationChecker.isHangoutJoinerAutomationServiceEnabled();

        assertThat(serviceEnabled).isFalse();
    }

    @Test
    public void whenChecking_thenRetrievesEnabledServicesFromAccessibilityManager() {

        automationChecker.isHangoutJoinerAutomationServiceEnabled();

        verify(accessibilityManager).getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_VISUAL);
    }

    private static class TestAccessibilityServiceInfo extends AccessibilityServiceInfo {

        private final String id;

        TestAccessibilityServiceInfo(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

    }

}
