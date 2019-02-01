`main` method is in SlackDeath.kt 

arg0 is your slack token with the correct user privalidges to read messages in channels

This hacky script will query the slack API to find all channels that haven't had a message posted in 3 months. It will then archive each of these channels.

You can hit the API limits with this script, but running the script twice is ok, wait two mins between runs to allow the slack rate limit to reset.

If you have so many channels that the rate limit is hit before it starts archiving - pull requests welcome!