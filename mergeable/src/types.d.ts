declare module "*.json" {
    const value: any;
    export default value;
}

declare interface Secrets {
    gitHub: GitHubSecrets;
    slack: SlackSecrets;
}

declare interface SlackSecrets {
    token: string;
    recipient: string;
}

declare interface GitHubSecrets {
    repoOwner: string;
    repoName: string;
    token: string;
}

declare namespace GitHubApi {

    export interface Repository {
        listPullRequests(options: any): any;
        getPullRequest(prNumber: number): Promise<any>;
    }

    export interface Options {
        token: string
    }

    class Instance {
        constructor(options: Options);
        getRepo(repoOwner: string, repoName: string): Repository;
    }

    export interface Factory {
        new (options: Options): Instance;
    }

}

declare module "github-api" {
    var gitHubApi: GitHubApi.Factory;
    export = gitHubApi;
}

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

