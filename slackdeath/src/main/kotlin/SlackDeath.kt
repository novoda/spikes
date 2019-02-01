import com.squareup.moshi.Moshi
import khttp.get
import java.time.ZonedDateTime
import java.util.*

data class JsonChannelsList(
        val channels: List<JsonChannel>
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

private var isDebug = true

/**
 * Arg 0 is your slack api token with user access (to be able to read channels)
 */
fun main(args: Array<String>) {
    val moshi = Moshi.Builder().build()
    val jsonChannelsAdapter = moshi.adapter(JsonChannelsList::class.java)
    val jsonMessagesAdapter = moshi.adapter(JsonMessages::class.java)

    val slackToken = args[0]

    val list = get("https://slack.com/api/channels.list?token=$slackToken&exclude_archived=true&pretty=1")

    val jsonChannelsList: JsonChannelsList = jsonChannelsAdapter.fromJson(list.jsonObject.toString()) as JsonChannelsList;
    debugPrint("${list.statusCode} - ${list.jsonObject}")
    val channels = jsonChannelsList.channels
    val archivable = arrayListOf<ArchiveableChannel>()

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

        val jsonMessages: JsonMessages = jsonMessagesAdapter.fromJson(messages.jsonObject.toString()) as JsonMessages
        if (jsonMessages.messages.isEmpty()) {
            archivable.add(ArchiveableChannel(c.id, c.name_normalized))
        }
    }

    println("Archive Channels")
    for (archiveChannel in archivable) {
        println(archiveChannel)
        val archivedResult = get("https://slack.com/api/channels.archive?token=$slackToken&channel=${archiveChannel.id}&pretty=1")
        debugPrint("${archivedResult.statusCode} ")
        debugPrint("${archiveChannel.name} ")
        debugPrint("${archivedResult.jsonObject}.")
    }
    println("Done. Archived ${archivable.size} channels.")
}

private fun debugPrint(string: String) {
    if (isDebug) {
        println(string)
    }
}

