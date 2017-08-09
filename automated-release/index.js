const Github = require('github-release')
const Artifacts = require('artifact-collect')
const GitBranch = require('git-branch')

const doRelease = async (params) => {
    const git = new GitBranch()
    const branchRef = await checkout(git, params)

    // writeVersion()
    const artifacts = params.artifacts()

    await push(git, branchRef, params)
    const github = new Github(params.auth.gitHubToken)
    await githubRelease(github, artifacts, params)
}

const checkout = (git, params) => {
    const checkoutOptions = {
        baseBranch: params.branches.fromBranch,
        newBranch: params.branches.releaseBranch,
        githubToken: params.auth.gitHubToken,
        path: params.repo.localPath,
        repoUrl: `https://github.com/${params.repo.owner}/${params.repo.name}.git`
    }
    return git.checkout(checkoutOptions)
}

const push = (git, branchRef, params) => {
    const pushOptions = {
        githubToken: params.auth.githubToken,
        remote: 'origin',
        commit: {
            authorName: 'mario',
            authorEmail: 'test@novoda.com',
            message: 'upping versions for release'
        }
    }
    return git.push(branchRef, pushOptions)
}

const githubRelease = (github, artifacts, params) => {
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
    return github.performRelease(repoOptions, prOptions, releaseOptions, artifacts)
}

module.exports = {
    release: doRelease
}
