import com.squareup.moshi.Moshi
import khttp.get
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.time.ZonedDateTime

data class JsonChannelsList(
        val channels: List<JsonChannel>?
)

data class JsonChannel(
        val id: String,
        val name_normalized: String,
        val is_archived: Boolean,
        val is_private: Boolean
)

data class JsonMessages(
        val messages: List<JsonMessage>
)

data class JsonMessage(
        val username: String,
        val text: String,
        val ts: String
)

data class ArchiveableChannel(
        val id: String,
        val name: String
)

/**
 * Arg 0 is your slack api token with user access (to be able to read channels)
 */
fun main(args: Array<String>) {
    val moshi = Moshi.Builder().build()
    val jsonChannelsAdapter = moshi.adapter(JsonChannelsList::class.java)
    val jsonMessagesAdapter = moshi.adapter(JsonMessages::class.java)

    val slackToken = args.getOrNull(0)
            ?: throw IllegalArgumentException("Needs a slack token as the first argument")

    val list = get("https://slack.com/api/channels.list?token=$slackToken&exclude_archived=true&pretty=1")

    debugPrint("${list.statusCode} - ${list.jsonObject}")
    val jsonChannelsList = jsonChannelsAdapter.fromJson(list.jsonObject.toString())
            ?: throw NullPointerException("No channels list could be parsed from response ${list.jsonObject}")
    val channels: List<JsonChannel> = jsonChannelsList.channels
            ?: throw NullPointerException("No channels list could be parsed from response ${list.jsonObject}")
    val channelsToArchive = arrayListOf<ArchiveableChannel>()

    val threeMonthsAgo = ZonedDateTime.now().minusMonths(3).toEpochSecond()
    println("Finding channels to archive")
    for (c in channels) {
        debugPrint("Channel ${c.name_normalized} ")
        if (c.is_private) {
            debugPrint("private, skipped.")
            continue
        }
        if (c.is_archived) {
            debugPrint("archived already.")
            continue
        }
        val channelId = c.id
        val messages = get("https://slack.com/api/channels.history?token=$slackToken&channel=$channelId&oldest=$threeMonthsAgo&count=1")

        val jsonMessages = jsonMessagesAdapter.fromJson(messages.jsonObject.toString()) as JsonMessages
        if (jsonMessages.messages.isEmpty()) {
            channelsToArchive.add(ArchiveableChannel(c.id, c.name_normalized))
        }
    }

    println("Archiving channels")
    for (archiveChannel in channelsToArchive) {
        debugPrint("About to archive ${archiveChannel.name}")
        val archivedResult = get("https://slack.com/api/channels.archive?token=$slackToken&channel=${archiveChannel.id}&pretty=1")
        println("Archived ${archiveChannel.name}")
        debugPrint("${archivedResult.statusCode} ")
        debugPrint("${archiveChannel.name} ")
        debugPrint("${archivedResult.jsonObject}.")
    }
    println("Done. Archived ${channelsToArchive.size} channels.")
}

private fun debugPrint(string: String) {
    println(string)
}

