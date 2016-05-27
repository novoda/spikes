/* global jest */

var Linking = {
  addEventListener: jest.genMockFunction(),
  removeEventListener: jest.genMockFunction()
}

module.exports = Linking
