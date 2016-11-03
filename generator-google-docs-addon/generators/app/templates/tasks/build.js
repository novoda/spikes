'use strict';

const connect = require('gulp-connect');

module.exports = (gulp, config) => {

  gulp.task('build', ['build:web', 'copy:shared', 'copy:config', 'copy:plugin', 'bower-web-dependencies']);

  gulp.task('build:dev', ['build:web:dev'], () => {
    gulp.src(`${config.src}/**/*`)
      .pipe(connect.reload());
  });

};
