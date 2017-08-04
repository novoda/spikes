const GithubApi = require('github-api')
const request = require('request-promise-native')
const fs = require('fs')

module.exports = class GithubRelease {

    constructor(token) {
        this.github = new GithubApi({ token: token })
        this.token = token
    }

    async performRelease(repoOptions, prOptions, releaseOptions, artifacts) {
        const repo = this.github.getRepo(repoOptions.owner, repoOptions.name)
        const pullRequest = await this._createReleasePullRequest(repo, prOptions)
        const release = await this._createRelease(repo, releaseOptions)
        await this._uploadAssets(repoOptions, release.id, artifacts)
        await this._commentOnPr(repoOptions, pullRequest.number, release.name, release.html_url)
    }

    _createReleasePullRequest(repo, request) {
        const createOptions = {
            title: request.title,
            body: request.body,
            head: request.fromBranch,
            base: request.intoBranch
        }
        return repo.createPullRequest(createOptions).then((result) => result.data)
    }

    _createRelease(repo, release) {
        const releaseOptions = {
            tag_name: release.tag,
            target_commitish: release.target,
            name: release.title,
            body: release.body,
            draft: release.isDraft,
            prerelease: release.isPreRelease
        }
        return repo.createRelease(releaseOptions).then((result) => result.data)
    }

    async _uploadAssets(repoOptions, releaseId, artifacts) {
        const allArtifacts = artifacts.map((artifact) => this._uploadAsset(repoOptions, releaseId, artifact))
        return await Promise.all(allArtifacts)
    }

    _uploadAsset(repoOptions, releaseId, artifact) {
        const file = fs.readFileSync(artifact.path);
        var options = {
            uri: `https://uploads.github.com/repos/${repoOptions.owner}/${repoOptions.name}/releases/${releaseId}/assets`,
            qs: {
                name: artifact.name
            },
            body: file,
            headers: {
                "Content-Type": 'application/zip',
                "Accept": "application/vnd.github.v3+json",
                "User-Agent": "novoda-release-tool",
                "Authorization": `token ${this.token}`
            }
        };
        return request.post(options);
    }

    _commentOnPr(repoOptions, pullRequestNumber, releaseName, releaseUrl) {
        const issueApi = this.github.getIssues(repoOptions.owner, repoOptions.name);
        const content = `You can find the artifacts in the latest release: [${releaseName}](${releaseUrl})`
        return issueApi.createIssueComment(pullRequestNumber, content)
    }

}
