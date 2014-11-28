# typewriter [![](http://ci.novoda.com/buildStatus/icon?job=typewriter)](http://ci.novoda.com/job/typewriter/lastBuild/console) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

A simple object relational mapper on top of Android's [Cursor][1] and [ContentProvider][2].

## Description

The aim of `typewriter` is to bring type safety to untyped Android APIs.

At the moment this is work-in-progress in a early stage, so only type safety on top of [Cursor][1] has been implemented, but the long term goal is to add types to other classes, such as [Intent][3] for example.

The main class is `CursorList` which is an implementation of a `List` that acts as a typed lazy list on a `Cursor`, allowing you to iterate over large data sets from the DB. `CursorList` implements `Closeable`, meaning you need to close it once you're done with it (this will close the `Cursor`).


## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

```groovy
repositories {
  jcenter()
}

dependencies {
  compile 'com.novoda:typewriter:0.0.3'
}
```


## Simple usage

Here is a simple example that shows how to get a list of contacts from the contacts list in an Android device:

First create the class that models the object:

```java
public class Contact {
    public String display_name;
    public String last_time_contacted;

    public Contact() {
    }
}
```

Then query for the data you need:

```java
Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
```

And voilà, you've got the typed list from the `Cursor`:

```java
CursorList<Contact> typedCursor = new CursorList<Contact2>(cursor, Contact.class);
for (Contact c : typedCursor) {
  Log.i("TEST", c.display_name + " " + c.last_time_contacted);
}
typedCursor.close();
```

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/novoda/typewriter/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/novoda/typewriter/wiki)
 * Looking for community help, browse the already asked [Stack Overflow Questions](http://stackoverflow.com/questions/tagged/support-typewriter) or use the tag: `support-typewriter` when posting a new question


 [1]: http://developer.android.com/reference/android/database/Cursor.html
 [2]: http://developer.android.com/reference/android/content/ContentProvider.html
 [3]: http://developer.android.com/reference/android/content/Intent.html
