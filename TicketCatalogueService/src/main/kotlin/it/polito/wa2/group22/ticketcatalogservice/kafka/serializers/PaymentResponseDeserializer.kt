package it.polito.wa2.group22.ticketcatalogservice.kafka.serializers

import  com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentRes
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class PaymentResponseDeserializer : Deserializer<PaymentRes> {

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): PaymentRes? {
        log.info("Deserializing PaymentResponse...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to PaymentResponse"),
                Charsets.UTF_8
            ), PaymentRes::class.java
        )
    }

    override fun close() {}

}