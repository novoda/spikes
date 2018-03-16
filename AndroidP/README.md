# Android P Spikes

## [Display coutout support](https://developer.android.com/preview/features.html#cutout)


Notches are called Display Cutouts on Android and Android P is adding support for them

In Android P you can simulate a display coutout with three different form factors.

![](https://developer.android.com/preview/images/emulator-devoptions-cutout_2x.png)


> Tall display cutout


When your activity screen goes behind the cutout you can specify how it should behave:

* [`LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT` ](https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT)

    > The window is allowed to extend into the DisplayCutout area, only if the DisplayCutout is fully contained within the status bar. Otherwise, the window is laid out such that it does not overlap with the DisplayCutout area.

    > In practice, this means that if the window did not set FLAG_FULLSCREEN or SYSTEM_UI_FLAG_FULLSCREEN, it can extend into the cutout area in portrait. Otherwise (i.e. fullscreen or landscape) it is laid out such that it does overlap the cutout area.
 
    > The usual precautions for not overlapping with the status bar are sufficient for ensuring that no important content overlaps with the DisplayCutout.

* [`LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS)

    > The window is always allowed to extend into the DisplayCutout area, even if fullscreen or in landscape.

    > The window must make sure that no important content overlaps with the DisplayCutout.

* [`LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER)

    > The window is never allowed to overlap with the DisplayCutout area.
    > This should be used with windows that transiently set SYSTEM_UI_FLAG_FULLSCREEN to avoid a relayout of the window when the flag is set or cleared.



The new [DisplayCutout](https://developer.android.com/reference/android/view/DisplayCutout.html) class lets you find out the location and shape of the non-functional areas where content shouldn't be displayed. 

For example, if a view in your layout falls behind a cutout area you can get the bounds of it and layout your view below the cutout

```kotlin
titleView.setOnApplyWindowInsetsListener { view, windowInsets ->
	windowInsets.displayCutout?.let {
	    val safeInsetTop = windowInsets.displayCutout.safeInsetTop
	    titleView.y = titleView.top + safeInsetTop.toFloat()	    
	}
	windowInsets
}
```
            