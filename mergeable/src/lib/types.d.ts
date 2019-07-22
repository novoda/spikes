interface GitHub {
    fetchOpenPullRequests: (githubSecrets: GitHubSecrets) => Promise<any>;
}

interface Slack {
    post: (recipient: string) => (message: string) => Promise<any>;
}

interface Result {
    isCalculating: boolean;
    prs: any[];
}

