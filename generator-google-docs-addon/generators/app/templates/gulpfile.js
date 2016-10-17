'use strict';

const gulp = require('gulp');

const glob = require('glob');
const config = require('./gulpfile.config.json');

const tasks = glob.sync(`${config.tasks}/**/*.js`);
tasks.forEach(file => {
  require(file)(gulp, config);
});
