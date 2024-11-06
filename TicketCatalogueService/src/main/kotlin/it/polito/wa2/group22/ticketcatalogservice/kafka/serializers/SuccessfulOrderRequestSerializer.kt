package it.polito.wa2.group22.ticketcatalogservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.ticketcatalogservice.entities.SuccessfulOrderReq
import org.apache.kafka.common.errors.SerializationException
import org.slf4j.LoggerFactory
import org.apache.kafka.common.serialization.Serializer


class SuccessfulOrderRequestSerializer : Serializer<SuccessfulOrderReq> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: SuccessfulOrderReq?): ByteArray? {
        log.info("Serializing SuccessfulOrderRequest...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing SuccessfulOrderRequest to ByteArray[]")
        )
    }

    override fun close() {}
}