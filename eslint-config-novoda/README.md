eslint-config-novoda
====================

_Shared ESLint rules for Novoda NodeJS projects tested with Jasmine._

## How to install

```
$ npm install --save-dev \
    eslint \
    eslint-plugin-jasmine \
    eslint-plugin-json \
    eslint-plugin-promise \
    eslint-config-standard \
    eslint-config-novoda
```

## How to use

In your project folder, create  `.eslintrc.json` file with the following content:

```json
{
    "extends": "eslint-config-novoda"
}
```

Running `eslint` from that folder will apply the shared Novoda rules.

You can still customise some rules by changing your `.eslintrc.json`.

## Default rules

* ES6 is enabled by default
* The project is assumed to be a NodeJS project
* Jasmine globals are automatically accepted
* Only single quotes are accepted
* Semi-colons are mandatory
* Proper spacing between keywords is enforced
* Braces must be followed by a new line
* `const` must be used anywhere the variable never changes
* Indentation must be with 4 spaces
* Triple equals `===` must always be used
