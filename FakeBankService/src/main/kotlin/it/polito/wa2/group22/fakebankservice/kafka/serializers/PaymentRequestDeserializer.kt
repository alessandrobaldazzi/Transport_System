package it.polito.wa2.group22.fakebankservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.fakebankservice.entities.PaymentRequest
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class PaymentRequestDeserializer : Deserializer<PaymentRequest> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): PaymentRequest? {
        log.info("Deserializing PaymentRequest...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to PaymentRequest"),
                Charsets.UTF_8
            ), PaymentRequest::class.java
        )
    }

    override fun close() {}

}