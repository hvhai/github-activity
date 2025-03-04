package com.codehunter.github_activity_kotlin

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.util.*


object InstantAsStringSerializer : kotlinx.serialization.KSerializer<Instant> {
    override val descriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor(
            "com.codehunter.InstantAsString",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

object EventTypeAsStringSerializer : kotlinx.serialization.KSerializer<EventType> {
    override val descriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor(
            "com.codehunter.EventTypeAsString",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: EventType) {
        encoder.encodeString(value.eventText)
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): EventType {
        return EventType.fromText(decoder.decodeString())
    }
}

object PayloadAsStringSerializer : kotlinx.serialization.KSerializer<String> {
    override val descriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("payload", PrimitiveKind.STRING)

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): String {
        val jsonElement = kotlinx.serialization.json.JsonElement.serializer().deserialize(decoder)
        return jsonElement.toString()
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GhEvent(
    val id: String,
    @Serializable(with = EventTypeAsStringSerializer::class)
    val type: EventType,
    val actor: Actor,
    val repo: Repo,
    @JsonNames("created_at")
    @Serializable(with = InstantAsStringSerializer::class)
    val createdAt: Instant,
    @Serializable(with = PayloadAsStringSerializer::class)
    val payload: String
) {
    fun buildMessage(): String {
        val jsonPayload = Json.parseToJsonElement(payload)
        return when (type) {
            EventType.PUSH_EVENT -> {
                val commitsArray = (jsonPayload as JsonObject)["commits"] as JsonArray
                val commitCount = commitsArray.size
                "Pushed $commitCount commits to ${repo.name}"
            }

            EventType.WATCH_EVENT -> "Starred ${repo.name}"
            EventType.ISSUES_EVENT -> {
                val action = (jsonPayload as JsonObject)["action"] as JsonPrimitive
                "${action.content.replaceFirstChar { it.uppercase() }} an issue in ${repo.name}"
            }

            EventType.ISSUE_COMMENT_EVENT -> {
                val action = (jsonPayload as JsonObject)["action"] as JsonPrimitive
                "${action.content.replaceFirstChar { it.uppercase() }} an issue comment in ${repo.name}"
            }

            EventType.PULL_REQUEST_EVENT -> {
                val action = (jsonPayload as JsonObject)["action"] as JsonPrimitive
                "${action.content.replaceFirstChar { it.uppercase() }} a pull request in ${repo.name}"
            }

            EventType.PULL_REQUEST_REVIEW_EVENT -> {
                val review = jsonPayload.jsonObject["review"]
                    ?.jsonObject?.get("user")
                    ?.jsonObject?.get("login")
                    ?.jsonPrimitive?.content
                "$review was requested for review in ${repo.name}"
            }

            EventType.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
                val review = jsonPayload.jsonObject["comment"]
                    ?.jsonObject?.get("user")
                    ?.jsonObject?.get("login")
                    ?.jsonPrimitive?.content
                "$review commented for a PR in ${repo.name}"
            }

            EventType.CREATE_EVENT -> {
                val refType = (jsonPayload as JsonObject)["ref_type"] as JsonPrimitive
                "Created a ${refType.content} in ${repo.name}"
            }

            EventType.DELETE_EVENT -> {
                val refType = (jsonPayload as JsonObject)["ref_type"] as JsonPrimitive
                "Deleted a ${refType.content} in ${repo.name}"
            }

            EventType.MEMBER_EVENT -> {
                val action = (jsonPayload as JsonObject)["action"] as JsonPrimitive
                val user = jsonPayload.jsonObject["member"]?.jsonObject?.get("login") as JsonPrimitive
                "${action.content.replaceFirstChar { it.uppercase() }} $user to ${repo.name}"
            }

            EventType.FORK_EVENT -> {
                val user = jsonPayload.jsonObject["forkee"]
                    ?.jsonObject?.get("owner")
                    ?.jsonObject?.get("login")
                    ?.jsonPrimitive?.content
                "$user  ${repo.name}"
            }

            else -> "Unsupported event type: $type, payload: $payload"
        }

    }
}

@Serializable
enum class EventType(val eventText: String) {
    COMMIT_COMMENT_EVENT("CommitCommentEvent"),
    CREATE_EVENT("CreateEvent"),
    DELETE_EVENT("DeleteEvent"),
    FORK_EVENT("ForkEvent"),
    GOLLUM_EVENT("GollumEvent"),
    ISSUE_COMMENT_EVENT("IssueCommentEvent"),
    ISSUES_EVENT("IssuesEvent"),
    MEMBER_EVENT("MemberEvent"),
    PUBLIC_EVENT("PublicEvent"),
    PULL_REQUEST_EVENT("PullRequestEvent"),
    PULL_REQUEST_REVIEW_EVENT("PullRequestReviewEvent"),
    PULL_REQUEST_REVIEW_COMMENT_EVENT("PullRequestReviewCommentEvent"),
    PULL_REQUEST_REVIEW_THREAD_EVENT("PullRequestReviewThreadEvent"),
    PUSH_EVENT("PushEvent"),
    RELEASE_EVENT("ReleaseEvent"),
    SPONSORSHIP_EVENT("SponsorshipEvent"),
    WATCH_EVENT("WatchEvent"),
    UNKNOWN_EVENT("UnknownEvent");

    companion object {
        fun fromText(text: String): EventType {
            return entries.find { it.eventText == text } ?: UNKNOWN_EVENT
        }
    }
}

@Serializable
data class Actor(val id: Int, val login: String)

@Serializable
data class Repo(val id: Int, val name: String)

class GhEventService {
    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun getEvents(username: String): List<GhEvent> {
        val request = Request.Builder()
            .url("https://api.github.com/users/$username/events")
            .build();
        client.newCall(request).execute().use { response ->
            require(response.isSuccessful) {
                "Failed to get events: statusCode=${response.code}: Message=${response.message}"
            }
            val bodyString = response.body?.string()
            return bodyString?.let { body -> json.decodeFromString<List<GhEvent>>(body) }
                ?: emptyList()
        }
    }
}

class GithubActivityCli {

    fun start() {
        try {
            print("github-activity ")

            val input = Scanner(System.`in`).nextLine()
            requireNotNull(input) { "Invalid input" }
            val command = input.split(" ")[0]
            if (command == "exit") {
                println("Exiting...")
                return
            } else if (input.split(" ").size > 1) {
                println("Invalid command")
            } else {
                val username = input
                val events = GhEventService().getEvents(username)
                println("Output:")
                events.forEach {
                    println(" - ${it.buildMessage()}")
                }
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
        start()
    }
}

fun main() {
    GithubActivityCli().start()
}
