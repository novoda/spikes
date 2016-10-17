'use strict';

const connect = require('gulp-connect');
const openURL = require('open');

module.exports = (gulp, config) => {

  gulp.task('start:server', ['build:dev'], function() {
    connect.server({
      root: [`${config.tmp}/web`, `${config.tmp}/lib`, '.'],
      livereload: true,
      host: `${config.host}`,
      port: config.port
    });
  });

  gulp.task('start:client', ['start:server'], function() {
    openURL(`http://${config.host}:${config.port}`);
  });

  gulp.task('watch', function() {
    gulp.watch([`${config.src}/**/*`, `${config.test}/**/*`], ['build:dev']);
  });

  gulp.task('serve', ['start:client', 'watch']);

};
