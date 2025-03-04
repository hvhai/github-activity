package com.codehunter.github_activity_kotlin

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.Instant

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
                val commitsArray = jsonPayload.jsonObject["commits"]?.jsonArray
                val commitCount = commitsArray?.size
                "Pushed $commitCount commits to ${repo.name}"
            }

            EventType.WATCH_EVENT -> "Starred ${repo.name}"

            EventType.ISSUES_EVENT -> {
                val action = jsonPayload.jsonObject["action"]?.jsonPrimitive?.content
                "${action?.replaceFirstChar { it.uppercase() }} an issue in ${repo.name}"
            }

            EventType.ISSUE_COMMENT_EVENT -> {
                val action = jsonPayload.jsonObject["action"]?.jsonPrimitive?.content
                "${action?.replaceFirstChar { it.uppercase() }} an issue comment in ${repo.name}"
            }

            EventType.PULL_REQUEST_EVENT -> {
                val action = jsonPayload.jsonObject["action"]?.jsonPrimitive?.content
                "${action?.replaceFirstChar { it.uppercase() }} a pull request in ${repo.name}"
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
                val refType = jsonPayload.jsonObject["ref_type"]?.jsonPrimitive?.content
                "Created a $refType in ${repo.name}"
            }

            EventType.DELETE_EVENT -> {
                val refType = jsonPayload.jsonObject["ref_type"]?.jsonPrimitive?.content
                "Deleted a $refType in ${repo.name}"
            }

            EventType.MEMBER_EVENT -> {
                val action = jsonPayload.jsonObject["action"]?.jsonPrimitive?.content
                val user = jsonPayload.jsonObject["member"]?.jsonObject?.get("login")?.jsonPrimitive?.content
                "${action?.replaceFirstChar { it.uppercase() }} $user to ${repo.name}"
            }

            EventType.FORK_EVENT -> {
                val user = jsonPayload.jsonObject["forkee"]
                    ?.jsonObject?.get("owner")
                    ?.jsonObject?.get("login")
                    ?.jsonPrimitive?.content
                "$user  ${repo.name}"
            }

            EventType.RELEASE_EVENT -> "Released a new version of ${repo.name}"

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

