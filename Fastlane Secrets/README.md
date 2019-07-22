# Fastlane secrets

Adds a plugin to be able to generate secrets from environment variables, for example in a CI.

To test this, make sure to run `export SECRET_apiKey=12345; bundle exec fastlane custom_lane` first, which generates a key with the name `apiKey` and the value `12345`! 
