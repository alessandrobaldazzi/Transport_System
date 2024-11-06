package it.polito.wa2.group22.travelerservice.kafka.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.travelerservice.kafka.dto.NewRegistrationDTO
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory



class NewRegistrationDeserializers : Deserializer<NewRegistrationDTO> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): NewRegistrationDTO? {
        log.info("Deserializing NewRegistration...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing NewRegistrationDTO"),
                Charsets.UTF_8
            ), NewRegistrationDTO::class.java
        )
    }

    override fun close() {}
}