Register your chrome device:

https://cast.google.com/publish/#/signup

Save your chrome device id to your build file:

https://github.com/novoda/Spikes/blob/master/ChromeCastExample/app/build.gradle#L16

Add the activities etc to your AndroidManifest:<br/>
*this will be removed if we update the aar to have them*

https://github.com/novoda/Spikes/blob/master/ChromeCastExample/app/src/main/AndroidManifest.xml

Define the factory that instantiates the VideoCastManager:

https://github.com/novoda/Spikes/blob/master/ChromeCastExample/app/src/main/java/com/blundell/chromecastexample/app/MyApplication.java

Add the LocalPlayerActivity:<br/>
*This is what plays the stream locally (VideoView) then when selected sends to chrome*

https://github.com/novoda/Spikes/blob/master/ChromeCastExample/app/src/main/java/com/blundell/chromecastexample/app/cast/LocalPlayerActivity.java

Create the intent with all details about the Video you want to stream:

https://github.com/novoda/Spikes/blob/master/ChromeCastExample/app/src/main/java/com/blundell/chromecastexample/app/MainActivity.java#L26

Done!!
=====
(*may have missed minor steps like adding menu resources ;-)*)
