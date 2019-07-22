const Application = require('spectron').Application
const chai = require('chai')
const chaiAsPromised = require('chai-as-promised')
const electronPath = require('electron')
const path = require('path')

chai.should()
chai.use(chaiAsPromised)

describe('Application launch', function () {
  beforeEach(function () {
    this.app = new Application({
      path: electronPath,
      args: [path.join(__dirname, '..')]
    })

    return this.app.start()
  })

  beforeEach(function () {
    chaiAsPromised.transferPromiseness = this.app.transferPromiseness
  })

  afterEach(function () {
    if (this.app && this.app.isRunning()) {
      return this.app.stop()
    }
  })

  it('opens a window', function () {
    return this.app.client.waitUntilWindowLoaded()
      .getWindowCount().should.eventually.have.at.least(1)
  })

  it('has a title', function () {
    return this.app.client.waitUntilWindowLoaded()
      .getTitle().should.eventually.equal('Hello Electron')
  })
})
