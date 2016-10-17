'use strict';

const eslint = require('gulp-eslint');
const Karma = require('karma').Server;

module.exports = (gulp, config) => {

  gulp.task('lint', () => {
    return gulp
      .src([`${config.src}/**/*.js`])
      .pipe(eslint())
      .pipe(eslint.format())
      .pipe(eslint.failAfterError());
  });

  gulp.task('karma', (done) => {
    new Karma({
      configFile: `${__dirname}/../karma.conf.js`,
      singleRun: true
    }, done).start();
  });

  gulp.task('test', ['lint', 'karma']);

};
