/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react';
import { AppRegistry } from 'react-native';

var HelloWorldView = require('./core/views/helloworld.js');

AppRegistry.registerComponent('ReactTwitter', () => HelloWorldView);
