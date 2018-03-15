# Fastlane Example Project

This repo contains an ios test project to see how Fastlane and the fastFile example in [ios-demos]() repo works and how can you modify it for your project.

You will need to install fastlane in your computer first following [this guide](https://docs.fastlane.tools/getting-started/ios/setup/) you do not need to do `fastlane init` since fastlane file and configuration has already been created

## Test

The action to run the tests is called `scan` and the lane that runs the tests is `test` || `ui_tests` and it uses the `.env.default` environment.

`fastlane test` or `fastlane ui_tests`

Currently the Unit tests pass always and the UI test fail on purpose so you can see what the output message for failing tests is. 

## Beta build

## Release build

##
