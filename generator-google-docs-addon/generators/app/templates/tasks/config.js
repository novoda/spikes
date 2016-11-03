'use strict';

const inject = require('gulp-inject');

module.exports = (gulp, config) => {

  gulp.task('config', () => {
    return gulp.src([`${config.src}/shared/config/config.js`], {base: `${config.src}/`})
      .pipe(inject(gulp.src(`${config.src}/config.json`), {
        starttag: '/* inject:config */',
        endtag: '/* endinject */',
        transform: (filePath, file) => {
          const fileContents = file.contents.toString('utf8');
          return `var config = ${fileContents};`;
        }
      }))
      .pipe(gulp.dest(config.tmp));
  });

};
