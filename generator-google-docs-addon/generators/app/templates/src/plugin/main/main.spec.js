'use strict';

describe('Main', function() {

  var mockSpreadsheet, main;

  beforeEach(function() {
    mockSpreadsheet = new MockSpreadsheet();

    main = new Main(config, mockSpreadsheet);
  });

  describe('doSomething', function() {

    it('should create a new sidebar', function() {
      main.doSomething();

      expect(mockSpreadsheet.createSidebar).toHaveBeenCalled();
    });

  });

  describe('doSomethingElse', function() {

    it('should show an alert', function() {
      main.doSomethingElse();

      expect(mockSpreadsheet.showAlert).toHaveBeenCalled();
    });

  });

});
