const Github = require('github-release')
const GITHUB_TOKEN = '1234'

const repoOptions = {
    owner: 'novoda',
    name: 'spikes'
}

const prOptions = {
    title: 'my awesome release PR',
    body: 'Here\'s all the changes in the release',
    fromBranch: 'master',
    intoBranch: 'demo-production'
}

const releaseOptions = {
    tag: 'v1',
    target: 'master',
    title: 'super cool release',
    body: 'this is the release body wooo',
    isDraft: false,
    isPreRelease: false
}

const artifacts = [
    {
        name: 'test-file-1234.zip',
        path: './artifacts/single-file/testfile.zip'
    }
]

const github = new GithubRelease(GITHUB_TOKEN)
github.performRelease(repoOptions, prOptions, releaseOptions, artifacts)
    .then(console.log)
    .catch(console.log)
