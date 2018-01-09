# node-watch [![Status](https://travis-ci.org/yuanchuan/node-watch.svg?branch=master)](https://travis-ci.org/yuanchuan/node-watch "See test builds")

A wrapper and enhancements for [fs.watch](http://nodejs.org/api/fs.html#fs_fs_watch_filename_options_listener) (with 0 dependencies).

[![NPM](https://nodei.co/npm/node-watch.png?downloads=true&downloadRank=true&stars=true)](https://nodei.co/npm/node-watch.png/)


## Installation

```bash
npm install node-watch
```

## Example

```js
var watch = require('node-watch');

watch('file_or_dir', { recursive: true }, function(evt, name) {
  console.log('%s changed.', name);
});
```

This is a completely rewritten version, **much faster** and in a more **memory-efficient** way.
So with recent nodejs under OS X or Windows you can do something like this:

```js
// watch the whole disk
watch('/', { recursive: true }, console.log);
```


## Why?

* Some editors will generate temporary files which will cause the callback function to be triggered multiple times.
* When watching a single file the callback function will only be triggered once.
* <del>Missing an option to watch a directory recursively.</del>
* Recursive watch is not supported on Linux or in older versions of nodejs.


## Events

The events provided by the callback function is either `update` or `remove`, which is less confusing to `fs.watch`'s `rename` and `change`.

```js
watch('./', function(evt, name) {

  if (evt == 'update') {
    // on create or modify
  }

  if (evt == 'remove') {
    // on delete
  }

});
```

## Options

The usage and options of `node-watch` is fully compatible with [fs.watch](https://nodejs.org/dist/latest-v7.x/docs/api/fs.html#fs_fs_watch_filename_options_listener).
* `persistent: <Boolean>` default = **true**
* `recursive: <Boolean>` default = **false**
* `encoding: <String>` default = **'utf8'**

##### Extra options

* `filter: <RegExp | Function>` filter using regular expression or custom function.

```js
// watch only for json files
watch('./', { filter: /\.json$/ }, console.log);

// ignore node_modules
watch('./', {
  recursive: true,
  filter: function(name) {
    return !/node_modules/.test(name);
  }
}, console.log);
```

## Watcher object

The watch function returns a [fs.FSWatcher](https://nodejs.org/api/fs.html#fs_class_fs_fswatcher) like object as the same as `fs.watch` (>= v0.4.0).

```js
var watcher = watch('./', { recursive: true });

watcher.on('change', function(evt, name) {
  // callback
});

watcher.on('error', function(err) {
  // handle error
});

// close
watcher.close();

// is closed?
watcher.isClosed()
```
The watcher object is also an instance of [EventEmitter](https://nodejs.org/dist/latest-v7.x/docs/api/events.html#events_class_eventemitter).
This's a list of methods for watcher specifically:

* `.on`
* `.once`
* `.emit`
* `.close`
* `.listeners`
* `.setMaxListeners`
* `.getMaxListeners`

##### Extra methods
* `.isClosed` detect if the watcher is closed


## Known issues

**Windows, node < v4.2.5**

  * Failed to detect `remove` event
  * Failed to get deleted filename or directory name

## Misc

#### 1. Watch multiple files or directories in one place
```js
watch(['file1', 'file2'], console.log);
```

#### 2. Customize watch command line tool
```js
#!/usr/bin/env node

// https://github.com/nodejs/node-v0.x-archive/issues/3211
require('epipebomb')();

var watcher = require('node-watch')(
  process.argv[2] || './', { recursive: true }, console.log
);

process.on('SIGINT', watcher.close);
```
Monitoring chrome from disk:
```bash
$ watch / | grep -i chrome
```

## License
MIT

Copyright (c) 2012-2017 [yuanchuan](https://github.com/yuanchuan)

