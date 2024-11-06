package it.polito.wa2.group22.fakebankservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.fakebankservice.entities.PaymentResponse
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.slf4j.LoggerFactory

class PaymentResponseSerializer : Serializer<PaymentResponse> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: PaymentResponse?): ByteArray? {
        log.info("Serializing PaymentResponse...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing PaymentResponse to ByteArray[]")
        )
    }

    override fun close() {}

}