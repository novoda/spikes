package com.novoda.staticanalysis.internal.findbugs

import com.novoda.staticanalysis.internal.QuietLogger
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsPlugin

public class QuietFindbugsPlugin extends FindBugsPlugin {

    @Override
    protected Class<FindBugs> getTaskType() {
        Task
    }

    static class Task extends FindBugs {
        @Override
        public Logger getLogger() {
            QuietLogger.INSTANCE
        }
    }

}
