'use strict';

/* exported MockSpreadsheet */
function MockSpreadsheet() {

  return jasmine.createSpyObj('mockSpreadsheet', [
    'createSidebar',
    'showAlert'
  ]);

}
