const OPEN_PRS = { state: 'open' }

const fetchOpenPullRequests = (gitHubApi: GitHubApi.Instance) => async (githubSecrets: GitHubSecrets): Promise<any> => {
    const repo: GitHubApi.Repository = createRepo(gitHubApi)(githubSecrets)
    const prSummaries = await repo.listPullRequests(OPEN_PRS)
    const prDetailsPromises = prSummaries.data.map(pr => pr.number)
        .map(prNumber => repo.getPullRequest(prNumber))
    return Promise.all(prDetailsPromises)
}

const createRepo = (gitHubApi: GitHubApi.Instance) => (githubSecrets: GitHubSecrets): GitHubApi.Repository => {
    return gitHubApi.getRepo(githubSecrets.repoOwner, githubSecrets.repoName)
}

export const GitHub = {
    create: (gitHubApi: GitHubApi.Instance): GitHub => {
        return {
            fetchOpenPullRequests: fetchOpenPullRequests(gitHubApi)
        }
    }
}
