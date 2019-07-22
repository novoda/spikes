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