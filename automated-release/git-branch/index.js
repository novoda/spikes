const Git = require("nodegit");

module.exports = class GitBranch {

    async checkout(options) {
        const repo = await this._clone(options.repoUrl, options.githubToken, options.path)
        await repo.checkoutBranch(options.baseBranch)
        const branchHeadCommit = await repo.getHeadCommit()
        const releaseBranchRef = await repo.createBranch(options.newBranch, branchHeadCommit.id())
        await repo.checkoutRef(releaseBranchRef)
        return repo
    }

    _clone(repoUrl, githubToken, path) {
        const cloneOptions = {
            fetchOpts: {
                callbacks: this._createAuth(githubToken)
            }
        }
        return Git.Clone(repoUrl, path, cloneOptions)
    }

    async push(repo, pushOptions) {
        await this._commitAll(repo, pushOptions.commit)
        const remote = await repo.getRemote(pushOptions.remote);
        const currentBranch = await repo.getCurrentBranch()
        const currentBranchName = currentBranch.name()
        return remote.push(
            [`${currentBranchName}:${currentBranchName}`],
            { callbacks: this._createAuth(pushOptions.githubToken) }
        )
    }

    async _commitAll(repo, commitOptions) {
        const index = await repo.refreshIndex()
        await index.addAll()
        await index.write()
        const id = await index.writeTree()
        const signature = Git.Signature.create(
            commitOptions.authorName,
            commitOptions.authorEmail,
            Date.now(),
            0
        );
        return repo.createCommit(null, signature, signature, commitOptions.message, id, [])
    }

    _createAuth(githubToken) {
        return {
            certificateCheck: function () { return 1; },
            credentials: function () {
                return Git.Cred.userpassPlaintextNew(githubToken, "x-oauth-basic");
            }
        }
    }

}

