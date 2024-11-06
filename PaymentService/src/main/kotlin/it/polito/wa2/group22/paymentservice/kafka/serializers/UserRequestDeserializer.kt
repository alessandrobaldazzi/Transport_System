package it.polito.wa2.group22.paymentservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.paymentservice.requests.UserRequest
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class UserRequestDeserializer : Deserializer<UserRequest> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): UserRequest? {
        log.info("Deserializing UserRequest...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to PaymentResponse"),
                Charsets.UTF_8
            ), UserRequest::class.java
        )
    }

    override fun close() {}
}