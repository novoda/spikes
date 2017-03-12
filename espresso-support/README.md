# espresso-support

Includes custom rules for testing Views in isolation and running tests with Google TalkBack enabled.

The library is split into two components:

- core - main espresso dependency (`androidTestCompile`)
- extras - optional, depending on which classes you use (`debugCompile`/`espressoCompile`)

The `extras` dependency adds some components to your app (hence it's not `androidTestCompile`), e.g. the `ViewActivity`.

## Testing views in isolation

Use the `ViewTestRule`, passing in a layout resource. It'll inflate the resource into the `ViewActivity` with `MATCH_PARENT` for both dimensions. You can use `rule.getView()` to obtain an instance of the View and it'll be typed to the class you specified.

```java
@Rule
public ViewTestRule<MovieItemView> rule = new ViewTestRule<>(R.layout.test_movie_item_view);
```

You can write BDD style tests here, highlighting the expected behaviour for your custom views, using a mixture of Espresso ViewActions and Mockito verifies:

```java
@Test
public void givenViewIsUpdatedWithDifferentMovie_whenClicking_thenListenerDoesNotGetFiredForOriginalMovie() {
    givenMovieItemViewIsBoundTo(EDWARD_SCISSORHANDS);
    givenMovieItemViewIsBoundTo(NOT_EDWARD_SCISSORHANDS);

    onView(withId(R.id.movie_item_button_play)).perform(click());

    verify(movieItemListener, never()).onClickPlay(eq(EDWARD_SCISSORHANDS));
}

private void givenMovieItemViewIsBoundTo(final Movie movie) {
    viewTestRule.bindViewUsing(new ViewTestRule.Binder<MovieItemView>() {
        @Override
        public void bind(MovieItemView view) {
            view.bind(movie);
        }
    });
}
```

:warning: This rule requires the `extras` module to be included for the app under test, so it can open the `ViewActivity`.

## Testing behaviour with TalkBack enabled

Often, our apps will behave differently when TalkBack is enabled to offer a more streamlined experience for users of screen readers.

Use either `TalkBackViewTestRule` or `TalkBackActivityTestRule` - TalkBack will be enabled before each test is run and disabled after each test finishes.

:warning: This rule requires the `extras` module to be included for the app under test, so it can open the `TalkBackStateSettingActivity`.
:warning: Toggling TalkBack state requires the `WRITE_SECURE_SETTINGS` permission being set for the app under test.

```bash
adb shell pm grant com.novoda.movies android.permission.WRITE_SECURE_SETTINGS
```

If the app is installed and the permission granted, you can enable/disable TalkBack via adb with the following actions:

```bash
$ adb shell am start -a "com.novoda.talkbacktoggle.ENABLE_TALKBACK"
$ adb shell am start -a "com.novoda.talkbacktoggle.DISABLE_TALKBACK"
```

You can also do the same with an Intent:

```java
Intent intent = new Intent("com.novoda.talkbacktoggle.ENABLE_TALKBACK")
context.startActivity(intent);
```

## Demo

You can run the demo tests with the following commands:

```bash
./gradlew demo:installDebug; adb shell pm grant com.novoda.movies android.permission.WRITE_SECURE_SETTINGS; adb shell am start -a "com.novoda.talkbacktoggle.DISABLE_TALKBACK"; ./gradlew demo:cAT; adb shell am start -a "com.novoda.talkbacktoggle.DISABLE_TALKBACK";
```

You have to install the app first to set the permission. We also disable TalkBack before and after the tests so we don't mess up non-TalkBack tests.

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/talkback-toggle/issues) first to see if we are working on it // TODO: waiting on repo to exist


