/* global global, jest */

global.__DEV__ = true
global.__fbBatchedBridgeConfig = {
  remoteModuleConfig: [],
  localModulesConfig: []
}

global.fetch = jest.fn()
global.Promise = require('promise')

jest.setMock('ErrorUtils', {
  setGlobalHandler () {
    // TODO
  }
})

import NativeModules from './NativeModules.mock'
import Linking from './Linking.mock'
jest.setMock('NativeModules', NativeModules)
jest.setMock('Linking', Linking)
