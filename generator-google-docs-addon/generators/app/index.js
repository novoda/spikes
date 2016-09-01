'use strict';
const yeoman = require('yeoman-generator');
const chalk = require('chalk');
const yosay = require('yosay');
const fs = require('q-io/fs');
const gapps = require('node-google-apps-script');

const OPTION_SKIP_FILE_ID = 'skip-file-id';
const OPTION_SKIP_API = 'skip-api';
const OPTION_SKIP_AUTH = 'skip-auth';
const OPTION_SKIP_INSTALL_MESSAGE = 'skip-install-message';
const OPTION_SKIP_INSTALL = 'skip-install';

module.exports = yeoman.Base.extend({
  constructor: function() {
    yeoman.Base.apply(this, arguments);

    this.option(OPTION_SKIP_FILE_ID);
    this.option(OPTION_SKIP_API);
    this.option(OPTION_SKIP_AUTH);
    this.option(OPTION_SKIP_INSTALL_MESSAGE);
    this.option(OPTION_SKIP_INSTALL);
  },

  prompting: {
    init: function() {
      // Have Yeoman greet the user.
      return this.log(yosay(
        'Welcome to the outstanding ' + chalk.red('google-docs-addon') + ' generator!'
      ));
    },

    info: function() {
      return this.prompt([{
        type: 'input',
        name: 'projectName',
        message: 'Name of your project:',
        store: true
      }, {
        type: 'input',
        name: 'projectDescription',
        message: 'Description for your project:',
        store: true
      }, {
        type: 'input',
        name: 'projectHomepage',
        message: 'Homepage for your project:',
        store: true
      }]).then((props) => {
        this.props = this.props || {};
        this.props.projectName = props.projectName;
        this.props.projectDescription = props.projectDescription;
        this.props.projectHomepage = props.projectHomepage;
        return this.props;
      });
    },

    googleAppsScript: function() {
      this.log('You are required to have a Google Apps Script project already created. If you don\'t, go to ' +
        'https://script.google.com and create one.');

      const addToProps = (config, name) => {
        return (obj) => {
          this.props = this.props || {};
          this.props[name] = obj[name];
          return this.props;
        }
      };

      const promptConfig = (config, promptConfiguration, validationFn) => {
        const checkedValidationFn = (value) => {
          if (validationFn) {
            if (promptConfiguration.when && promptConfiguration.when()) {
              return validationFn(value);
            }
          }
          return value;
        };
        const promptFn = (config) => {
          return this
            .prompt([promptConfiguration])
            .then(checkedValidationFn)
            .then(addToProps(config, promptConfiguration.name))
            .catch(() => {
              return promptFn(config);
            });
        };
        return promptFn(config);
      };

      const promptFileId = (config) => {
        return promptConfig(config,
          {
            type: 'input',
            name: 'fileId',
            message: 'Type the file ID of your Google Apps Script project (e.g., for ' +
            '"https://script.google.com/d/1e7mfWrjs5zHV-1IOLAlaTTn6TNlezL2cqzXydb2NEpfXyBMNswmRI8FW/edit" the file ID is ' +
            '"1e7mfWrjs5zHV-1IOLAlaTTn6TNlezL2cqzXydb2NEpfXyBMNswmRI8FW").',
            default: '1e7mfWrjs5zHV-1IOLAlaTTn6TNlezL2cqzXydb2NEpfXyBMNswmRI8FW',
            when: () => {
              return !this.options.skipFileId;
            }
          });
      };

      const promptHasEnabledApi = (config) => {
        return promptConfig(config,
          {
            type: 'confirm',
            name: 'hasEnabledApi',
            message: 'Open your Google Apps Script project, go to "Resources" --> "Developers Console Project", then ' +
            'click on the blue link and enable the Drive API.\nHave you done that?',
            default: false,
            when: () => {
              return !this.options.skipApi;
            }
          },
          (props) => {
            if (props.hasEnabledApi === true) {
              return Promise.resolve(props);
            }
            this.log.error('You need to enable the Drive API on the Developers Console Project associated with your ' +
              'Google Apps Script project!');
            return Promise.reject();
          });
      };

      const promptSecretFile = (config) => {
        return promptConfig(config,
          {
            type: 'input',
            name: 'secretFile',
            message: 'In the Developers Console Project, create a new OAuth Client ID of type "Other", then download ' +
            'the credentials JSON and type the path here.',
            default: '/Users/fra/Downloads/client_secret_1011541283870-14svn4n0pdlm1clhkg66oovup9dcu75m.apps.googleusercontent.com.json',
            when: () => {
              return !this.options.skipAuth;
            }
          },
          (props) => {
            props.secretFile = props.secretFile.trim();
            return fs
              .isFile(props.secretFile)
              .then((isFile) => {
                if (isFile) {
                  return props;
                }
                const errorMessage = `The file ${props.secretFile} does not exist or cannot be read.`;
                this.log.error(errorMessage);
                throw new Error(errorMessage);
              });
          });
      };

      return promptFileId()
        .then(promptHasEnabledApi)
        .then(promptSecretFile);
    }

  },

  configuring: {
    gappsAuth: function() {
      const getUserHome = function() {
        return process.env[(process.platform == 'win32') ? 'USERPROFILE' : 'HOME'];
      };

      const gappsAuthenticate = () => {
        if (this.options.skipAuth) {
          return;
        }
        const gappsAuthConfig = getUserHome() + '/.gapps';
        return fs
          .exists(gappsAuthConfig)
          .then((exists) => {
            if (exists) {
              this.log.error(`An authentication file for "gapps" already exists at "${gappsAuthConfig}. Remove it to re-authenticate.`);
              process.exit(1);
              return;
            }
            return gapps.auth(this.props.secretFile, true);
          });
      };

      return gappsAuthenticate();
    }
  },

  writing: {
    gappsConfig: function() {
      if (this.options.skipFileId) {
        return;
      }
      this.log('Creating configuration file for gapps.');
      const gappsConfig = {
        path: 'build',
        fileId: this.props.fileId
      };
      var gappsConfigPath = this.destinationPath('gapps.config.json');
      this.fs.writeJSON(gappsConfigPath, gappsConfig, null, 2);
    },

    configFiles: function() {
      this.readmeTemplate = this.templatePath('README.md');
      this.packageTemplate = this.templatePath('package.json');
      this.bowerTemplate = this.templatePath('bower.json');
      this.karmaTemplate = this.templatePath('karma.conf.js');
    },

    copyFiles: function() {
      this.log('Copying files.');
      this.fs.copy([
          this.templatePath('**'),
          '!' + this.readmeTemplate,
          '!' + this.packageTemplate,
          '!' + this.bowerTemplate,
          '!' + this.karmaTemplate
        ],
        this.destinationPath('./'),
        {globOptions: {dot: true}}
      );
    },

    compileTemplates: function() {
      this.log('Compiling templates.');
      this.fs.copyTpl([
          this.readmeTemplate,
          this.packageTemplate,
          this.bowerTemplate,
          this.karmaTemplate
        ],
        this.destinationPath('./'),
        {
          projectName: this.props.projectName,
          projectDescription: this.props.projectDescription,
          projectHomepage: this.props.projectHomepage
        }
      );
    }

  },

  install: function() {
    this.installDependencies({
      skipMessage: this.options.skipInstallMessage,
      skipInstall: this.options.skipInstall
    });
  }
});
