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
        name: 'spikes',
        localPath: CLONE_PATH
    },
    branches: {
        baseBranch: 'master',
        releaseBranch: 'release-2.5',
        endBranch: 'production'
    },
    release: {
        tag: 'v1',
        title: 'super cool release',
        body: 'this is the release body wooo'
    },
    pr: {
        title: 'my awesome release PR',
        body: 'Here\'s all the changes in the release'
    },
    generateArtifacts: (collector) => {
        return [
            collector.collectFile('./artifacts/single-file/testfile.zip', ARTIFACTS_OUTPUT_PATH, 'test-file-1234.zip'),
            collector.collectDirectory('./artifacts/mappings', ARTIFACTS_OUTPUT_PATH, 'mappings.zip')
        ]
    },
    versionProperties: {
        increment: 50,
        path: `${CLONE_PATH}version.properties`
    }
}

release(config).then(() => {
    fs.removeSync(CLONE_PATH)
    fs.removeSync(ARTIFACTS_OUTPUT_PATH)
})
