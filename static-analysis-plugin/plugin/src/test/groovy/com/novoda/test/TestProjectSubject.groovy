package com.novoda.test

import com.google.common.truth.*

import javax.annotation.Nullable

class TestProjectSubject extends Subject<TestProjectSubject, TestProject> {

    private static final SubjectFactory<TestProjectSubject, TestProject> FACTORY = newFactory()
    private static SubjectFactory<TestProjectSubject, TestProject> newFactory() {
        new SubjectFactory<TestProjectSubject, TestProject>() {
            @Override
            TestProjectSubject getSubject(FailureStrategy failureStrategy, TestProject testProject) {
                return new TestProjectSubject(failureStrategy, testProject)
            }
        }
    }

    private TestProjectSubject(FailureStrategy failureStrategy, @Nullable TestProject testProject) {
        super(failureStrategy, testProject)
    }

    public static TestProjectSubject assumeThat(TestProject testProject) {
        TruthJUnit.assume().about(FACTORY).that(testProject)
    }

    public void isAndroidProject() {
        if (!(actual() instanceof TestAndroidProject)) {
            failureStrategy.fail(String.format('project was instance of <%s>, but was instance of <%s> instead',
                    TestAndroidProject.simpleName,
                    actual().getClass().simpleName))
        }
    }
}
