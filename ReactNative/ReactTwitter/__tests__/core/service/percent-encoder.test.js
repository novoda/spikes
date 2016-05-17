jest.unmock('../../../core/service/percent-encoder.js')
var PercentEncoder = require('../../../core/service/percent-encoder.js')

describe('PercentEncoder', () => {
  describe('encode', () => {
    it('should encode emoji', () => {
      expect(PercentEncoder.encode('☃')).toBe('%E2%98%83')
    })
    it('should encode exclamation mark', () => {
      expect(PercentEncoder.encode('An encoded string!')).toBe('An%20encoded%20string%21')
    })
    it('should encode URLs', () => {
      expect(PercentEncoder.encode('https://api.twitter.com')).toBe('https%3A%2F%2Fapi.twitter.com')
    })
    it('should encode query', () => {
      expect(PercentEncoder.encode('include_entities=true&oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1318622958&oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&oauth_version=1.0&status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21'))
        .toBe('include_entities%3Dtrue%26oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1318622958%26oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26oauth_version%3D1.0%26status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521')
    })
    it('should encode base64 string', () => {
      expect(PercentEncoder.encode('tnnArxj06cWHq44gCs1OSKk/jLY=')).toBe('tnnArxj06cWHq44gCs1OSKk%2FjLY%3D')
    })
    it('should encode plus sign', () => {
      expect(PercentEncoder.encode('Ladies + Gentlemen')).toBe('Ladies%20%2B%20Gentlemen')
    })
    it('should encode enumeration', () => {
      expect(PercentEncoder.encode('Dogs, Cats & Mice')).toBe('Dogs%2C%20Cats%20%26%20Mice')
    })
  })
})
