jest.unmock('../../../core/service/deep-linking-facade.ios.js')

const DeepLinkingFacade = require('../../../core/service/deep-linking-facade.ios.js')

describe('Deep linking facade (iOS)', () => {
  let facade

  beforeEach(() => {
    facade = new DeepLinkingFacade()
  })

  it('should start listening', () => {
    expect(facade.listenForDeepLinking()).toBeDefined()
  })
})
