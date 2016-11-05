package com.novoda.gradle.diffdependencytask.ext

import com.novoda.gradle.diffdependencytask.ConditionalDependency
import org.gradle.api.Task

import java.util.regex.Pattern

enum ExtensionRegister implements Runnable {

    INSTANCE

    private ExtensionRegister() {
        // private ctor
    }

    @Override
    void run() {
        registerTaskExtension()
    }

    private static void registerTaskExtension() {
        Task.metaClass.ifDiffMatches << { Pattern[] patterns ->
            new ConditionalDependency(delegate, patterns)
        }
    }
}

