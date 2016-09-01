'use strict';

/* exported Main */
function Main(config, spreadsheet) {
  this.config = config;
  this.spreadsheet = spreadsheet;
}

Main.prototype.doSomething = function() {
  this.spreadsheet.createSidebar('My Sidebar', 'Awesome Function', 'sidebar-awesome.html');
};

Main.prototype.doSomethingElse = function() {
  this.spreadsheet.showAlert('Message', 'This is a message that logs the API base path: ' + this.config.api);
};
