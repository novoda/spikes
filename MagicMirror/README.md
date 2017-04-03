# Magic Mirror
Make your mirror even smarter!
This app is meant to be installed on phones, tablets or TV sticks used in smart mirrors or similar always-on dashboards.

**_As seen at Liverpool MakeFest 2016!_**

##Features
* Time and Date
* Weather forecast for the current day
* Live Tweet updates based on configurable query (by default set to `novoda`)
* Demo TODO list
* Face recognition with smile detector (on a dedicated screen)

##Screenshots

| Info screen | Loking for face | Face sad | Face neutral | Face happy | Face oh-my-God-so-happy | 
|---|---|---|---|---|---|
|![](screenshots/info_screen.png)|![](screenshots/0_looking.png)|![](screenshots/1_sad.png)|![](screenshots/2_neutral.png) |![](screenshots/3_happy.png)|![](screenshots/4_omgSoHappy.png)|

##TODO
* Use the actual coordinates of the device for weather forecast (now set to Liverpool)
* Apply a switch between TODO-list display and Tweet feed
* More animations for the different face emotions

##Development
In order to use all the features of the app when building it from source add the `secrets.properties` file with the keys needed, as below:
```
# https://home.openweathermap.org/api_keys
openWeatherMapKey=1234567890qwertyuipo

# Twitter keys
twitterConsumerKey=123456789
twitterConsumerSecret=123456789
twitterAccessToken=123456789
twitterAccessTokenSecret=123456789

```

When deployed on an emulator, it is possible to quickly test the different emotions using the keyboard keys 0-4.
