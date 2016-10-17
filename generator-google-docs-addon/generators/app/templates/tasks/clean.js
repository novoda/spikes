'use strict';

const clean = require('gulp-clean');

module.exports = (gulp, config) => {

  gulp.task('clean:plugin', function() {
    return gulp
      .src(`${config.build}/plugin`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:web', function() {
    return gulp
      .src(`${config.build}/web`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:web:dev', function() {
    return gulp
      .src(`${config.tmp}/web`, {read: false})
      .pipe(clean());
  });

  gulp.task('clean:shared', function() {
    return gulp
      .src([`${config.build}/shared`, `${config.build}/*.js`], {read: false})
      .pipe(clean());
  });

  gulp.task('clean:bower-web-dependencies', function() {
    return gulp
      .src(`${config.build}/bower_components`, {read: false})
      .pipe(clean());
  });

};
