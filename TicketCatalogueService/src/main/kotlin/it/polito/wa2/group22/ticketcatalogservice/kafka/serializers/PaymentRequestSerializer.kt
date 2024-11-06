package it.polito.wa2.group22.ticketcatalogservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentReq
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory

class PaymentRequestSerializer : Serializer<PaymentReq> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: PaymentReq?): ByteArray? {
        log.info("Serializing PaymentRequest...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing PaymentRequest to ByteArray[]")
        )
    }

    override fun close() {}

}