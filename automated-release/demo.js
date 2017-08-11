const release = require('./index').release
const fs = require('fs-extra')

const CLONE_PATH = __dirname + '/tmp/'
const VERSION_PROPERTIES_PATH = CLONE_PATH + 'version.properties'
const ARTIFACTS_OUTPUT_PATH = __dirname + '/outputs/'

const config = {
    auth: {
        gitHubToken: '1234'
    },
    repo: {
        owner: 'novoda',
        localPath: CLONE_PATH,
        remote: 'origin'
    },
    branches: {
        baseBranch: 'master',
        releaseBranch: 'release-2.5',
        endBranch: 'production'
    },
    release: {
        tag: 'v1',
        title: 'super cool release',
        body: 'this is the release body wooo',
        commit: {
            authorName: 'mario',
            authorEmail: 'test@novoda.com',
            message: 'upping versions for release'
        }
    },
    pr: {
        title: 'my awesome release PR',
        body: 'Here\'s all the changes in the release'
    },
    generateArtifacts: (collector) => {
        return Promise.resolve([
            collector.collectFile('./artifacts/single-file/testfile.zip', ARTIFACTS_OUTPUT_PATH, 'test-file-1234.zip'),
            collector.collectDirectory('./artifacts/mappings', ARTIFACTS_OUTPUT_PATH, 'mappings.zip')
        ])
    },
    androidVersionProperties: {
        versionCodeIncrement: 50,
        path: `${CLONE_PATH}version.properties`,
        versionName: '1.0.0'
    }
}

release(config).then(() => {
    fs.removeSync(CLONE_PATH)
    fs.removeSync(ARTIFACTS_OUTPUT_PATH)
})
