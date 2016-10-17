// Karma configuration

const gulpConfig = require('./gulpfile.config.json');

module.exports = function(config) {
  const karmaConfig = {

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      `${gulpConfig.src}/**/*.js`,
      'bower_components/es6-promise/promise.js',
      'bower_components/lodash/lodash.js'
    ],


    // list of files to exclude
    exclude: [
      `${gulpConfig.src}/plugin/index.js`,
      `${gulpConfig.src}/web/**/*.js`
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'coverage', 'junit'],


    coverageReporter: {
      reporters: [
        {type: 'html', dir: 'coverage/'},
        {type: 'cobertura', dir: 'coverage/', subdir: '.'}
      ]
    },


    junitReporter: {
      outputDir: 'coverage', // results will be saved as $outputDir/$browserName.xml
      outputFile: 'TEST-karma.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
      suite: '<%= projectName %>', // suite will become the package name attribute in xml testsuite element
      useBrowserName: false // add browser name to report and classes names
    },


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity
  };

  // preprocess matching files before serving them to the browser
  // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
  karmaConfig.preprocessors[`${gulpConfig.src}/**/*.js`] = ['coverage'];

  config.set(karmaConfig);
};
