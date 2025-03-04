package com.codehunter.github_activity_kotlin

import kotlinx.serialization.descriptors.PrimitiveKind
import java.time.Instant

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
