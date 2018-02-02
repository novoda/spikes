# Game of Life - Kotlin Multiplatform
---------------------------------

https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life

# common
business logic including unit tests which are run for all platforms

# common-jvm
jvm specific implementations

# common-js
js specific implementations

# terminal-jvm
jvm based terminal client

# android
JVM based Android client

Install to an Android device via `./gradlew android:installAll`

![gol](https://user-images.githubusercontent.com/1046688/34879875-fbe8cbd8-f7ae-11e7-8c33-21fbfac15e8b.gif)

# web
React web client

Run via `npm install & npm run serve` from the `web` folder.

![nyan cat](https://user-images.githubusercontent.com/1046688/34879934-279134dc-f7af-11e7-953b-f4e7f140c57d.gif)

# desktop-jvm
JVM based desktop client using TornadoFX

Run with `./gradlew desktop-jvm:run`

![gol](https://user-images.githubusercontent.com/517070/35706797-723ef2dc-07a7-11e8-857d-d4fc86a2e200.png)

