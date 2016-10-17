'use strict';
var path = require('path');
var assert = require('yeoman-assert');
var helpers = require('yeoman-test');

describe('generator-google-docs-addon:app', function () {
  before(function () {
    return helpers.run(path.join(__dirname, '../generators/app'))
      .withOptions({
        skipFileId: true,
        skipApi: true,
        skipAuth: true
      })
      .withPrompts({
        projectName: 'My project',
        projectDescription: 'Something something danger zone',
        projectHomepage: 'https://novoda.com'
      })
      .toPromise();
  });

  it('creates files', function () {
    assert.file([
      'gulpfile.js'
    ]);
  });
});
