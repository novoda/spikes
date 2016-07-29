package com.novoda.monkey

import com.novoda.gradle.command.Monkey

class TargetedMonkey extends Monkey {

    def packageName
    def logFileName

    @Override
    def getPackageName() {
        packageName
    }

    @Override
    protected handleCommandOutput(def text) {
        super.handleCommandOutput(text)
        saveToFile(text)
    }

    private saveToFile(def text) {
        new File(logFileName).text = text
    }
}
