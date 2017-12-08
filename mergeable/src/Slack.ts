const OPTIONS = {
    as_user: true
}

const post = (slackClient: any) => (recipient: string) => (message: string): Promise<any> => {
    return slackClient.chat.postMessage(recipient, message, OPTIONS)
}

export const Slack = {
    create: (slackClient: any): Slack => {
        return {
            post: post(slackClient)
        }
    }
}
