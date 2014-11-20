package hudson.tasks.test;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;
import hudson.model.Run.Summary;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Tests {@link Run#getBuildStatusSummary()}.
 * 
 * @author kutzi
 */
@SuppressWarnings("rawtypes")
public class BuildStatusSummaryTest {

    @Rule public JenkinsRule r = new JenkinsRule(); // to load AbstractTestResultAction.Summarizer

    private Run build;
    private Run prevBuild;

    @Before
    public void before() {
        mockBuilds(Run.class);
    }
    
    private void mockBuilds(Class<? extends Run> buildClass) {
        this.build = mock(buildClass);
        this.prevBuild = mock(buildClass);
        
        when(this.build.getPreviousBuild()).thenReturn(prevBuild);
        
        when(this.build.getBuildStatusSummary()).thenCallRealMethod();
    }
    
    @Test
    public void testBuildGotAFailingTest() {
        // previous build has no tests at all
        mockBuilds(AbstractBuild.class);
        
        when(this.build.getResult()).thenReturn(Result.UNSTABLE);
        when(this.prevBuild.getResult()).thenReturn(Result.SUCCESS);
        
        buildHasTestResult((AbstractBuild) this.build, 1);
        
        Summary summary = this.build.getBuildStatusSummary();
        
        assertTrue(summary.isWorse);
        assertEquals(Messages.Run_Summary_TestFailures(1), summary.message);
        
        
        // same thing should happen if previous build has tests, but no failing ones:
        buildHasTestResult((AbstractBuild) this.prevBuild, 0);
        summary = this.build.getBuildStatusSummary();
        assertTrue(summary.isWorse);
        assertEquals(Messages.Run_Summary_TestsStartedToFail(1), summary.message);
    }

    @Test
    public void testBuildGotNoTests() {
        // previous build has no tests at all
        mockBuilds(AbstractBuild.class);

        when(this.build.getResult()).thenReturn(Result.UNSTABLE);
        when(this.prevBuild.getResult()).thenReturn(Result.UNSTABLE);
        // Null test result action recorded
        when(((AbstractBuild) this.build).getAction(AbstractTestResultAction.class)).thenReturn(null);

        Summary summary = this.build.getBuildStatusSummary();

        assertFalse(summary.isWorse);
        assertEquals(hudson.model.Messages.Run_Summary_Unstable(), summary.message);

        // same thing should happen if previous build has tests, but no failing ones:
        buildHasTestResult((AbstractBuild) this.prevBuild, 0);
        summary = this.build.getBuildStatusSummary();
        assertFalse(summary.isWorse);
        assertEquals(hudson.model.Messages.Run_Summary_Unstable(), summary.message);
    }
    
    @Test
    public void testBuildEqualAmountOfTestsFailing() {
        mockBuilds(AbstractBuild.class);
        
        when(this.build.getResult()).thenReturn(Result.UNSTABLE);
        when(this.prevBuild.getResult()).thenReturn(Result.UNSTABLE);
        
        buildHasTestResult((AbstractBuild) this.prevBuild, 1);
        buildHasTestResult((AbstractBuild) this.build, 1);
        
        Summary summary = this.build.getBuildStatusSummary();
        
        assertFalse(summary.isWorse);
        assertEquals(Messages.Run_Summary_TestsStillFailing(1), summary.message);
    }
    
    @Test
    public void testBuildGotMoreFailingTests() {
        mockBuilds(AbstractBuild.class);
        
        when(this.build.getResult()).thenReturn(Result.UNSTABLE);
        when(this.prevBuild.getResult()).thenReturn(Result.UNSTABLE);
        
        buildHasTestResult((AbstractBuild) this.prevBuild, 1);
        buildHasTestResult((AbstractBuild) this.build, 2);
        
        Summary summary = this.build.getBuildStatusSummary();
        
        assertTrue(summary.isWorse);
        assertEquals(Messages.Run_Summary_MoreTestsFailing(1, 2), summary.message);
    }
    
    @Test
    public void testBuildGotLessFailingTests() {
        mockBuilds(AbstractBuild.class);
        
        when(this.build.getResult()).thenReturn(Result.UNSTABLE);
        when(this.prevBuild.getResult()).thenReturn(Result.UNSTABLE);
        
        buildHasTestResult((AbstractBuild) this.prevBuild, 2);
        buildHasTestResult((AbstractBuild) this.build, 1);
        
        Summary summary = this.build.getBuildStatusSummary();
        
        assertFalse(summary.isWorse);
        assertEquals(Messages.Run_Summary_LessTestsFailing(1, 1), summary.message);
    }
    
    private void buildHasTestResult(AbstractBuild build, int failedTests) {
        AbstractTestResultAction testResult = mock(AbstractTestResultAction.class);
        when(testResult.getFailCount()).thenReturn(failedTests);
        
        when(build.getAction(AbstractTestResultAction.class)).thenReturn(testResult);
    }
}
