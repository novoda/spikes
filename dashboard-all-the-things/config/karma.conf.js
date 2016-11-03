// Karma configuration file, see link for more information
// https://karma-runner.github.io/0.13/config/configuration-file.html

module.exports = function (config) {
  config.set({
    basePath: '..',
    frameworks: ['jasmine', 'angular-cli'],
    plugins: [
      require('karma-jasmine'),
      require('karma-phantomjs-launcher'),
      require('karma-junit-reporter'),
      require('karma-chrome-launcher'),
      require('karma-remap-istanbul'),
      require('angular-cli/plugins/karma')
    ],
    customLaunchers: {
      // chrome setup for travis CI using chromium
      Chrome_travis_ci: {
        base: 'Chrome',
        flags: ['--no-sandbox']
      }
    },
    files: [
      { pattern: './src/test.ts', watched: false }
    ],
    preprocessors: {
      './src/test.ts': ['angular-cli']
    },
    junitReporter: {
      outputDir: 'coverage', // results will be saved as $outputDir/$browserName.xml
      outputFile: 'TEST-karma.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
      suite: 'com.novoda.github.reports.dashboard', // suite will become the package name attribute in xml testsuite element
      useBrowserName: false // add browser name to report and classes names
    },
    remapIstanbulReporter: {
      reports: {
        html: 'coverage',
        cobertura: 'coverage/cobertura-coverage.xml'
      }
    },
    angularCliConfig: './angular-cli.json',
    reporters: ['progress', 'karma-remap-istanbul', 'junit'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['PhantomJS'],
    singleRun: false
  });
};
