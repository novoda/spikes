const Github = require('github-release')
const collector = require('artifact-collect')
const GitBranch = require('git-branch')
const AndroidProps = require('android-release-properties')

const doRelease = async (config) => {
    const git = new GitBranch()
    const branchRef = await checkout(git, config)

    updateVersionProperties(config.androidVersionProperties)
    const artifacts = await config.generateArtifacts(collector)

    await push(git, branchRef, config)
    const github = new Github(config.auth.gitHubToken)
    await githubRelease(github, artifacts, config)
}

const checkout = (git, config) => {
    const checkoutOptions = {
        baseBranch: config.branches.baseBranch,
        newBranch: config.branches.releaseBranch,
        githubToken: config.auth.gitHubToken,
        path: config.repo.localPath,
        repoUrl: `https://github.com/${config.repo.owner}/${config.repo.name}.git`
    }
    return git.checkout(checkoutOptions)
}

const updateVersionProperties = (androidVersionProperties) => {
    const androidProps = new AndroidProps()
    const versionOptions = {
        path: androidVersionProperties.path,
        increment: androidVersionProperties.versionCodeIncrement,
        versionName: androidVersionProperties.versionName
    }
    androidProps.update(versionOptions)
}

const push = (git, branchRef, config) => {
    const pushOptions = {
        githubToken: config.auth.gitHubToken,
        remote: config.repo.remote || 'origin',
        commit: {
            authorName: config.release.commit.authorName,
            authorEmail: config.release.commit.authorEmail,
            message: config.release.commit.message
        }
    }
    return git.push(branchRef, pushOptions)
}

const githubRelease = (github, artifacts, config) => {
    const repoOptions = {
        owner: config.repo.owner,
        name: config.repo.name
    }

    const prOptions = {
        title: config.pr.title,
        body: config.pr.body,
        fromBranch: config.branches.releaseBranch,
        intoBranch: config.branches.endBranch
    }

    const releaseOptions = {
        tag: config.release.tag,
        target: config.branches.releaseBranch,
        title: config.release.title,
        body: config.release.body,
        isDraft: false,
        isPreRelease: false
    }
    return github.performRelease(repoOptions, prOptions, releaseOptions, artifacts)
}

module.exports = {
    release: doRelease
}
