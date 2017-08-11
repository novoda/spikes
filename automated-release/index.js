const Github = require('github-release')
const collector = require('artifact-collect')
const GitBranch = require('git-branch')

const doRelease = async (config) => {
    const git = new GitBranch()
    const branchRef = await checkout(git, config)

    const artifacts = await config.generateArtifacts(collector)

    await push(git, branchRef, config)
    const github = new Github(config.auth.gitHubToken)
    await githubRelease(github, artifacts, config)
}

const checkout = (git, config) => {
    const checkoutOptions = {
        baseBranch: config.branches.fromBranch,
        newBranch: config.branches.releaseBranch,
        githubToken: config.auth.gitHubToken,
        path: config.repo.localPath,
        repoUrl: `https://github.com/${config.repo.owner}/${config.repo.name}.git`
    }
    return git.checkout(checkoutOptions)
}

const push = (git, branchRef, config) => {
    const pushOptions = {
        githubToken: config.auth.githubToken,
        remote: 'origin',
        commit: {
            authorName: 'mario',
            authorEmail: 'test@novoda.com',
            message: 'upping versions for release'
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
