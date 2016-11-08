generator-google-docs-addon 
===========================                        

[![NPM version][npm-image]][npm-url] 
[![Build Status][travis-image]][travis-url] 
[![Dependency Status][daviddm-image]][daviddm-url] 
[![Coverage percentage][coveralls-image]][coveralls-url]

> Generate structured and testable Google Docs Add-ons

---------------------------

## Why oh why?

Creating add-ons for Google Docs (and Sheets) looks easy at first sight, but becomes painful over time:

* there is no way to bundle dependencies
* all Web pages must contains scripts and styles inlined in the HTML
* there is no clear way to test scripts
* there is no template system to create common pages (e.g., sidebars and alerts)

This [Yeoman](http://yeoman.io) generator provides all the means to develop Google Docs add-ons with ease of mind:

* scaffolding of a thoughtful **project structure**
* server sample code uses manual **Dependency Injection**
* **easier tests** as all components are contained in separated directories
* **configuration** can be ignored in your version control and injected into the built files
* **coverage** outputs HTML, Cobertura and JUnit reports for a better CI integration
* pages can reference **external CSS and JS files**
* support for **template and partial pages**

## Installation

First, install [Yeoman](http://yeoman.io) and generator-google-docs-addon using [npm](https://www.npmjs.com/) (we assume
you have pre-installed [node.js](https://nodejs.org/)).

```bash
npm install -g yo
npm install -g generator-google-docs-addon
```

Then generate your new project:

```bash
yo google-docs-addon
```

### Questions

The generator will ask you the following questions:

1. The name of your project.
2. A description of your project.
3. The website URL of your project.
4. The file ID of your Google Apps Script project (e.g., for 
   "https://script.google.com/d/1e7mfWrjs5zHV-1IOLAlaTTn6TNlezL2cqzXydb2NEpfXyBMNswmRI8FW/edit" the file ID is 
   "1e7mfWrjs5zHV-1IOLAlaTTn6TNlezL2cqzXydb2NEpfXyBMNswmRI8FW").
5. Confirmation that you have enabled the Google Drive APIs for the Google Cloud project bound to the Apps Script 
   project.
6. Path of the OAuth 2.0 credentials JSON file that you need to create on the Google Cloud project.
        
### Options

You can set the following options for the generator:

* `--skip-file-id`, won't ask for the Google Apps Script project ID, you'd have to set it up yourself
* `--skip-api`, won't ask you if you have enabled the Google Drive API for the Cloud Project bound to the  Apps Script 
  project
* `--skip-auth`, won't ask for the JSON credentials file path and won't authorise your account for upload

You can generally skip these steps when you are re-running the generator on an already sey up project.

More options are available:

* `--skip-install`, won't install bower and NPM dependencies
* `--skip-install-message`, won't log the "Installing dependencies" message

## Understand the generated project

The generated project contains a `README.md` [(see README template)](generators/app/templates/README.md) detailing the 
directory structure and all tasks available to run.

[npm-image]: https://badge.fury.io/js/generator-google-docs-addon.svg
[npm-url]: https://npmjs.org/package/generator-google-docs-addon
[travis-image]: https://travis-ci.org/novoda/generator-google-docs-addon.svg?branch=master
[travis-url]: https://travis-ci.org/novoda/generator-google-docs-addon
[daviddm-image]: https://david-dm.org/novoda/generator-google-docs-addon.svg?theme=shields.io
[daviddm-url]: https://david-dm.org/novoda/generator-google-docs-addon
[coveralls-image]: https://coveralls.io/repos/novoda/generator-google-docs-addon/badge.svg
[coveralls-url]: https://coveralls.io/r/novoda/generator-google-docs-addon
