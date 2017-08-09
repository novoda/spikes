const release = require('./index')
const fs = require('fs-extra')

const CLONE_PATH = __dirname + '/tmp/'
const ARTIFACTS_PATH = __dirname + '/outputs/'

const params = {
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
    artifacts: () => {
        // create/read artifacts
        return [
            Artifacts.collectFile('./artifacts/single-file/testfile.zip', ARTIFACTS_PATH, 'test-file-1234.zip'),
            Artifacts.collectDirectory('./artifacts/mappings', ARTIFACTS_PATH, 'mappings.zip')
        ]
    },
    version: {
        increment: 50,
        path: `${CLONE_PATH}version.properties`
    }
}

release.release(params).then(() => {
    fs.removeSync(CLONE_PATH)
    fs.removeSync(ARTIFACTS_PATH)
})