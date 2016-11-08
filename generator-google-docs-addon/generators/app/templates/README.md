<%= projectName %>
==================

_<%= projectDescription %>_

---------------------------

### Pre-requisites

This module uses:

* [NodeJS](https://nodejs.org/en/) (v4.x LTS or later) as the execution environment
* [Gulp](http://gulpjs.com/) as a task runner
* [Karma](https://karma-runner.github.io/1.0/index.html) as a test runner
* [node-google-apps-script](https://github.com/danthareja/node-google-apps-script) to deploy the add-on
* [Bower](https://bower.io) to manage external dependencies

Therefore you need to install `gulp-cli` as a global package:

```shell
npm install -g gulp-cli node-google-apps-script
```

### Setup

#### Create your project on Google Apps Script           

The following instructions show you how to setup the project for the first time.

Create a Google Apps Script project:

* Go to [https://script.google.com](https://script.google.com) and create a new project
* Save the new project by giving it a name
* The project will be saved in your Google Drive, with a URL like `https://script.google.com/d/THIS_IS_THE_PROJECT_ID/edit`
* Take note of your project ID

#### Authenticate for deploy on Google Cloud Console

Open your Google Apps Script project and:

* Go to "Resources" --> "Developers Console Project"
* Click on the Google Cloud Project blue link

You will be redirected to the Google Cloud Project Console linked to your Google Apps Script Project:

* Enable the Google Drive API
* Create a new OAuth client ID Credential of type "Other"
* Download the credentials JSON file (e.g., `client_secret.json`)

You can now authenticate `gapps` with the JSON you downloaded:

* [Authenticate `gapps`](https://github.com/danthareja/node-google-apps-script#2-authenticate-gapps) using the 
  credentials JSON you just downloaded (e.g., `gapps auth client_secret.json`)
* [Initialise the configuration](https://github.com/danthareja/node-google-apps-script#31-an-existing-apps-script-project)
  with `gapps init THIS_IS_THE_PROJECT_ID -s build` using your Google Apps Script project ID

Now you're ready to develop on this project!

### Project structure

All source files are located under `src`. Components should be scoped to their own directory, containing the code 
itself, the specs, Web pages, styles, etc.

#### Bower dependencies

You can install external dependencies with [Bower](https://bower.io), they will be found and deployed to the Apps Script
project and/or included in your Web pages (see ["Regular page"](#regular-page) to learn how to inject bower dependencies
in your HTML).

You can change the `tasks/bower.js` file to ignore Bower dependencies so they are not shipped to Apps Script (they will 
still be injected into pages).

**IMPORTANT:** please note that files other than JS and CSS (such as images and fonts) will not be sent to Google Apps 
Script, since they cannot be referenced from Web pages. You should instead use hosted dependencies.

#### Shared code

The `shared` folder contains code that can run in the browser and in the Google Apps Script environment. All shared 
components are automatically made available to client and server code.

The `config` component contains a default JS object that will be overridden using contents from `src/config.json` (see 
[`src/config.sample.json`](src/config.sample.json)), so that you won't have to commit sensible configuration to version 
control.

#### Plugin code

The `plugin` folder contains code that can only run in the Google Apps Script environment. Make sure that everything
running here depends solely on Google Apps Script components, Bower dependencies or [shared code](#shared-code).

#### Web code

The `web` folder contains code that can run in any browser. Any CSS files under `web` will be shared across all Web
pages.
Every sub-directory contains a component (code-only or code and view).

##### Regular page

A page component directory contains:
* a HTML file (e.g., `bananas.html`)
* (optional) a CSS file with the same name as the HTML file (e.g., `bananas.css`)
* (optional) a JS page controller with the same name as the HTML plus the `.controller` (e.g., `bananas.controller.js`)

The HTML page can have the following placeholders, ending with `<!-- endinject -->`:
* `<!-- inject:bower:css -->`, when built it will contain all CSS from bower dependencies
* `<!-- inject:css -->`, when built it will contain all CSS from the `web` and the component folders
* `<!-- inject:bower:js -->`, when built it will contain all scripts from bower dependencies
* `<!-- inject:js -->`, when built it will contain all scripts from `shared` and `web` folders
* `<!-- inject:controller:js -->`, when built it will contain the component controller script

##### Templates and partials

This add-on supports adding templates and partials that will be compiled to single pages.

###### Template

To create a template page, e.g. `fruit`, create a `fruit` directory in `web` and create a template file ending with 
`.template.html` (you can optionally create a common CSS for all pages of this type):

* `fruit` will be the directory name of the template
* `fruit.template.html` will contain the HTML code common to all fruit pages
* `fruit.css` (optional) will contain the CSS styles for all fruit pages

```shell
$ mkdir `web/fruit` && cd $_
$ touch fruit.template.html fruit.css
```

Besides all placeholders available for ["regular pages"](#regular-page), you must also specify `<!-- inject:html -->`
that will be replaced by the partial page, when compiled.

###### Partial

To create a partial component to be injected into the template, give it the same name as the template, plus a dash and 
the partial name, e.g. `fruit-apple`:

* `fruit-apple` will be the directory name of the partial page
* `fruit-apple.partial.html` will contain the HTML code that will be injected into the template `fruit.html`
* `fruit-apple.css` (optional) will contain the CSS style for the `fruit-apple` page only
* `fruit-apple.controller.js` (optional) will contain the page controller script for the `fruit-apple` page

```shell
$ mkdir `web/fruit-apple` && cd $_
$ touch fruit-apple.partial.html fruit-apple.css fruit-apple.controller.js
```

### Dependency Injection

Every file usually represents a class, whose constructor accepts all of its dependencies, so we can easily mock them in
tests. Please always adhere to this pattern in order to avoid deploying and manually tests basic behaviours on Apps
Script.

### Available tasks

The available Gulp tasks are:
* `lint`, runs ESLint on the codebase, highlighting any syntax errors/warnings
* `karma`, runs all the tests (specs) in the codebase using PhantomJS (you can re-configure it for any other browser)
  and generates code coverage as an HTML website and as a Cobertura XML file
* `test`, runs `lint` and `karma` in parallel
* `build`, executes the tests and builds the uploadable project
* `upload`, builds the project and then uploads it to Google Drive
* `serve`, builds a development version of the project and starts a live reload server to test UI
