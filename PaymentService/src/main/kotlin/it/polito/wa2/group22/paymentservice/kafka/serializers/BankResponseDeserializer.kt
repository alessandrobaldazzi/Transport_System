package it.polito.wa2.group22.paymentservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.paymentservice.responses.BankResponse
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory

class BankResponseDeserializer : Deserializer<BankResponse>{

    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): BankResponse? {
        log.info("Deserializing BankResponse...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to PaymentResponse"),
                Charsets.UTF_8
            ), BankResponse::class.java
        )
    }

    override fun close() {}

}