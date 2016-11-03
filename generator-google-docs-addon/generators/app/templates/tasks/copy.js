'use strict';

module.exports = (gulp, config) => {

  gulp.task('copy:plugin', ['clean:plugin'], () => {
    return gulp
      .src([
        `${config.src}/*.js`,
        `${config.src}/plugin/**/*.js`,
        `!${config.src}/plugin/**/*.spec.js`,
        `!${config.src}/plugin/**/*.mock.js`,
      ], {
        base: `${config.src}`
      })
      .pipe(gulp.dest(`${config.build}/`));
  });

  gulp.task('copy:shared', ['test', 'clean:shared'], () => {
    return gulp
      .src([
        `${config.src}/shared/**/*.js`,
        `!${config.src}/shared/**/*.spec.js`,
        `!${config.src}/shared/**/*.mock.js`,
        `!${config.src}/shared/config/config.js`
      ], {
        base: `./${config.src}`
      })
      .pipe(gulp.dest(`${config.build}/`));
  });

  gulp.task('copy:config', ['config'], () => {
    return gulp
      .src([`${config.tmp}/shared/config/config.js`], {
        base: `${config.tmp}`
      })
      .pipe(gulp.dest(`${config.build}/`));
  });

};
