const GitReleaseBranch = require('./index')
const fs = require('fs');

const branch = new GitReleaseBranch()

const releaseOptions = {
    fromBranch: 'master',
    releaseBranch: 'a_release_branch',
    githubToken: '1234567',
    repoUrl: 'https://github.com/novoda/spikes.git',
    remote: 'origin'
}

const commitOptions = {
    authorName: 'mario',
    authorEmail: 'test@novoda.com',
    message: 'upping versions for release'
}

const createAndCommit = async () => {
    const repo = await branch.checkoutRelease(releaseOptions)
    fs.writeFileSync('./tmp/testfile.txt', "hello world, release commit content")
    await branch.pushRelease(repo, releaseOptions, commitOptions)
    branch.cleanUp()
}

createAndCommit().then(console.log).catch(console.log)
