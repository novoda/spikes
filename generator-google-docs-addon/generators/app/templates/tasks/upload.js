'use strict';

const shell = require('gulp-shell');

module.exports = (gulp, config) => {

  gulp.task('upload', ['build'], shell.task(['gapps upload']));

};
