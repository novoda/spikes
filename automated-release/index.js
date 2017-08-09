const Github = require('github-release')
const Artifacts = require('artifact-collect')
const GitBranch = require('git-branch')
const fs = require('fs-extra')

const CLONE_PATH = __dirname + '/tmp/'
const ARTIFACTS_PATH = __dirname + '/outputs/'

const params = {
    auth: {
        gitHubToken: '1234'
    },
    repo: {
        owner: 'novoda',
        name: 'spikes'
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
        // build apks
        return [
            Artifacts.collectFile('./artifacts/single-file/testfile.zip', ARTIFACTS_PATH, 'test-file-1234.zip'),
            Artifacts.collectDirectory('./artifacts/mappings', ARTIFACTS_PATH, 'mappings.zip')
        ]
    },
    version: {
        increment: 50
    }
}


release.release(params);

const git = new GitBranch()
const github = new GithubRelease(GITHUB_TOKEN)

const checkoutOptions = {
    baseBranch: params.branches.fromBranch,
    newBranch: params.branches.releaseBranch,
    githubToken: params.auth.gitHubToken,
    path: CLONE_PATH,
    repoUrl: `https://github.com/${params.repo.owner}/${params.repo.name}.git`
}

const pushOptions = {
    githubToken: GITHUB_TOKEN,
    remote: 'origin',
    commit: {
        authorName: 'mario',
        authorEmail: 'test@novoda.com',
        message: 'upping versions for release'
    }
}

const repoOptions = {
    owner: params.repo.owner,
    name: params.repo.name
}

const prOptions = {
    title: params.pr.title,
    body: params.pr.body,
    fromBranch: params.branches.releaseBranch,
    intoBranch: params.branches.endBranch
}

const releaseOptions = {
    tag: params.release.tag,
    target: params.branches.releaseBranch,
    title: params.release.title,
    body: params.release.body,
    isDraft: false,
    isPreRelease: false
}

const doRelease = async () => {
    const branchRef = await git.checkout(checkoutOptions)

    // make modifications & build artifacts
    fs.writeFileSync('./tmp/testfile.txt', "hello world, release commit content")
    const artifacts = params.artifacts()

    await git.push(branchRef, pushOptions)

    github.performRelease(repoOptions, prOptions, releaseOptions, artifacts)

    fs.removeSync(CLONE_PATH)
    fs.removeSync(ARTIFACTS_PATH)
}

doRelease.then(console.log).catch(console.log)
