fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## iOS
### ios test
```
fastlane ios test
```
Runs all the tests
### ios ui_tests
```
fastlane ios ui_tests
```
Runs all the UI tests
### ios beta_release
```
fastlane ios beta_release
```
Build Beta version and send to internal testing
### ios release_dev
```
fastlane ios release_dev
```
Build 
### ios release_testflight
```
fastlane ios release_testflight
```
Test, Build and send a release to Testflight
### ios code_coverage
```
fastlane ios code_coverage
```
Generate Unit Test Code Coverage
### ios new_sprint
```
fastlane ios new_sprint
```
Update the version number for a new sprint
### ios install_certificates
```
fastlane ios install_certificates
```
Install developer certificates and provisioning profiles

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
