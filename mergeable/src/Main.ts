import * as rawSecrets from '../secrets.json'
import { checkMergeability } from './Mergeable'
import { GitHub as gitHub } from './GitHub'
import { WebClient as SlackClient } from '@slack/client'
import { Slack } from './Slack'

const secrets: Secrets = (<any>rawSecrets)
const slackClient: any = new SlackClient(secrets.slack.token)
const slackPoster = Slack(slackClient)

const options: GitHubApi.Options = {
    token: secrets.gitHub.token
}

const gitHub = 

checkMergeability(slackPoster, gitHub, secrets).then(console.log).catch(console.log)