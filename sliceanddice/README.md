# Slice&Dice — Android P slices sample app

<img alt="Screenshots from the host app" src="http://blog.novoda.com/content/images/2018/03/sample_app.jpg" width="70%" />

Slice&Dice is a sample application that demonstrates what `Slice`s are and how to both [host](sliceanddice/host) and 
[provide](sliceanddice/provider) them using the slices compatibility library. The application has the same 
`minSdkVersion 24` as the compat library, meaning it should work on Android 7.0 and later, although in our testing we've
seen many crashes on pre-P Android versions. It is therefore **not supported** on Android versions 8.x and lower.

## Modules and roles

<img alt="App roles" src="http://blog.novoda.com/content/images/2018/03/roles-1.svg" width="60%" />

The sample is composed of two independent apps, a [**`host`**](sliceanddice/host) app which will display the slices, and a
[**`provider`**](sliceanddice/provider) app that produces the slices. There's a number of workarounds and hacks to avoid
bugs and crashes in the current implementation of the slices in the P platform and in the support library.

In order to display slices, the `host` app needs the `provider` app to be already installed on the device. If you run the
`host` app without having the `provider` app installed, you'll get an error message:

<img alt="The error message that the host app shows when the provider is not installed"
  src="https://user-images.githubusercontent.com/153802/37780897-974b9f06-2de7-11e8-8c2c-a55113f95dd2.png" 
  width="300px" />

## More on this
To read more about this topic, you can find two articles on the Novoda blog:

<a href="http://blog.novoda.com/android-p-slices-missing-documentation-part-1/" target=_blank border=0>
  <img alt="Android P Slices: the missing documentation — part 1" 
  src="https://user-images.githubusercontent.com/153802/37781202-2955c48a-2de8-11e8-92a9-ef3671b135ad.png" />
</a>  <a href="http://blog.novoda.com/android-p-slices-missing-documentation-part-2/" target=_blank border=0>
  <img alt="Android P Slices: the missing documentation — part 2" 
  src="https://user-images.githubusercontent.com/153802/37781272-457413d8-2de8-11e8-9266-63535e5cbe54.png" />
</a>

## License
Slice&Dice is released under the Apache 2.0 licence. Please refer to the [`LICENSE`](sliceanddice/LICENSE) file for the 
full terms and conditions of the licence.
