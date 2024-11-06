package it.polito.wa2.group22.paymentservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.paymentservice.requests.BankRequest
import org.apache.kafka.common.errors.SerializationException
import org.slf4j.LoggerFactory
import org.apache.kafka.common.serialization.Serializer

class BankRequestSerializer : Serializer<BankRequest>{
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: BankRequest?): ByteArray? {
        log.info("Serializing BankRequest...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing PaymentRequest to ByteArray[]")
        )
    }

    override fun close() {}
}