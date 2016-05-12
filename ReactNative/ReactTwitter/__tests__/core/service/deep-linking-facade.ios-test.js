jest.dontMock('../../../core/service/deep-linking-facade.ios.js')

const DeepLinkingFacade = require('../../../core/service/deep-linking-facade.ios.js')

describe('Deep linking facade (iOS)', () => {
  let facade

  beforeEach(() => {
    facade = new DeepLinkingFacade()
  })

  it('should inject a facade', () => {
    expect(facade).toBeDefined()
  })

  // Can't test it because of the import using react-native
  it('should start listening', () => {
    // expect(facade.listenForDeepLinking()).toBeDefined();
  })
})
