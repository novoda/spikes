import com.squareup.moshi.Moshi
import khttp.get
import java.time.ZonedDateTime

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
    println("${list.statusCode} - ${list.jsonObject}")
    val channels = jsonChannelsList.channels
    val archivable = arrayListOf<ArchiveableChannel>()

    println("Finding Archiveable Channels")
    for (c in channels) {
        println("Channel ${c.name_normalized} ")
        if (c.is_private) {
            println("private, skipped.")
            continue
        }
        if (c.is_archived) {
            println("archived already.")
            continue
        }
        val channelId = c.id;
        val THREE_MONTHS_AGO = ZonedDateTime.now().minusMonths(3).toEpochSecond()
        val msgs = get("https://slack.com/api/channels.history?token=$slackToken&channel=$channelId&oldest=$THREE_MONTHS_AGO&count=1")
//        print("messages ")
//        println(msgs.jsonObject)

        val jsonMessages: JsonMessages = jsonMessagesAdapter.fromJson(msgs.jsonObject.toString()) as JsonMessages
        if (jsonMessages.messages.isEmpty()) {
            archivable.add(ArchiveableChannel(c.id, c.name_normalized))
        }
    }

    println("Archive Channels")
    for (archiveChannel in archivable) {
        println(archiveChannel)
        val archivedResult = get("https://slack.com/api/channels.archive?token=$slackToken&channel=${archiveChannel.id}&pretty=1")
        print("${archivedResult.statusCode} ")
        print("${archiveChannel.name} ")
        print("${archivedResult.jsonObject}.")
    }
    println("done.")
    println("Archived ${archivable.size} channels.")
    println("Archived $archivable")
}


