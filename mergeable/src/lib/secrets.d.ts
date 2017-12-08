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