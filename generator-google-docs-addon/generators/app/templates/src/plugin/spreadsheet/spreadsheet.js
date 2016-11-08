'use strict';

/* exported Spreadsheet */
function Spreadsheet() {
  this.spreadsheet = SpreadsheetApp.getActiveSpreadsheet();
  this.ui = SpreadsheetApp.getUi();
}

Spreadsheet.prototype.createSidebar = function(name, title, htmlFile) {
  this.sidebars[name] = HtmlService
    .createHtmlOutputFromFile(htmlFile)
    .setTitle(title)
    .setSandboxMode(HtmlService.SandboxMode.IFRAME);
  return name;
};

Spreadsheet.prototype.showAlert = function(title, message) {
  this.ui.alert(title, message, this.ui.ButtonSet.OK);
};
