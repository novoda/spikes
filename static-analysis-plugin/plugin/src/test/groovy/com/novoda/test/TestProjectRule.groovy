package com.novoda.test

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

final class TestProjectRule implements TestRule {

    private final Closure<TestProject> projectFactory
    private TestProject project

    static TestProjectRule forJavaProject() {
        new TestProjectRule({ new TestJavaProject() })
    }

    static TestProjectRule forAndroidProject() {
        new TestProjectRule({ new TestAndroidProject() })
    }

    private TestProjectRule(Closure<TestProject> projectFactory) {
        this.projectFactory = projectFactory
    }

    public TestProject newProject() {
        project = projectFactory.call()
        return project
    }

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                base.evaluate()
                project?.deleteDir()
            }
        }
    }

}
