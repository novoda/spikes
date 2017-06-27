import * as rawSecrets from '../secrets.json'
import { checkMergeability } from './Mergeable'
import { GitHub } from './GitHub'
import * as GitHubApi from 'github-api'
import { WebClient as SlackClient } from '@slack/client'
import { Slack } from './Slack'

const createGitHub = (secrets: GitHubSecrets): GitHub => {
    const options: GitHubApi.Options = {
        token: secrets.token
    }
    return GitHub(new GitHubApi(options))
}

const createSlack = (secrets: SlackSecrets): Slack => {
    const slackClient: any = new SlackClient(secrets.token)
    return Slack(slackClient)
}

const secrets: Secrets = (<any>rawSecrets)
const slackPoster = createSlack(secrets.slack)
const gitHub = createGitHub(secrets.gitHub)

checkMergeability(slackPoster, gitHub, secrets).then(console.log).catch(console.log)
