'use strict';

/* exported onInstall, onOpen */

function onInstall() {
  onOpen();
}

function onOpen() {
  var main = buildMain_();
  // Use main to access top-level methods here
  main.doSomething();
}

function buildMain_() {
  var spreadsheet = new Spreadsheet();
  return new Main(config, spreadsheet);
}
