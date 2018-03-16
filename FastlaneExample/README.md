# Fastlane Example Project

This repo contains an ios test project to see how Fastlane and the fastFile example in [ios-demos]() repo works and how can you modify it for your project.

You will need to install fastlane in your computer first following [this guide](https://docs.fastlane.tools/getting-started/ios/setup/) you do not need to do `fastlane init` since fastlane file and configuration has already been created

## Test

The action to run the tests is called `scan` and the lane that runs the tests is `test` || `ui_tests` and it uses the `.env.default` environment.

`fastlane test` or `fastlane ui_tests`

Currently the Unit tests pass always and the UI test fail on purpose so you can see what the output message for failing tests is.

## Beta build

Beta builds the app using gym and uploads it to a testing service. In the case of this taste we did not upload the app anywhere. Just builds and sends a message to slack.

Beta uses the environment file `env.beta` there is everything configured that beta release needs.

`fastlane beta_release --env beta`

What the beta fastfile does not have and you should add in your project is [match](https://docs.fastlane.tools/actions/match/). Match stores certificates in a private secure method and allows you to do all the certificate signing by using a fastlane command.

Follow the set up instructions in [here](https://docs.fastlane.tools/actions/match/) and then add the following command to your beta lane.

`match(type: 'development')`

You will also need to add a command for where you want your beta to be uploaded, crashalytics, hockeyapp, etc

"fields": [
  {
    "title": "Version",
    "value": version_number,
    "short": false
  },
  {
    "title": "Build",
    "value": build_number,
    "short": false
    }
    ]


## Release build

##
