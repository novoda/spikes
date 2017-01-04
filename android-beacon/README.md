Android beacons
======

This projects shows how to use Eddystone beacons to receive a notification when entering or leaving the beacon range.

## Emulate beacons

If you don't have a physical beacon available, you can emulate one with the Beacon Toy app: https://play.google.com/store/apps/details?id=com.uriio 

## Configure your beacon to work with the Google Nearby services 

https://developers.google.com/beacons/get-started

## Configure your app to receive messages via the Google Nearby services

https://developers.google.com/nearby/messages/android/get-started

Attachments data format:
https://developers.google.com/nearby/notifications/attachment_data_format#free-form_app_intent 

## Configure secrets file

In order to be able to use the project, copy the `secrets.properties.example` file to `secret.properties` and fill in the Google Nearby API key.
