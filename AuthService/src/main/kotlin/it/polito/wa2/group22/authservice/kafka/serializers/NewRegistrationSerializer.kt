package it.polito.wa2.group22.authservice.kafka.serializers



import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.group22.authservice.dto.NewRegistrationDTO
import org.apache.kafka.common.errors.SerializationException
import org.slf4j.LoggerFactory
import org.apache.kafka.common.serialization.Serializer

class NewRegistrationSerializer : Serializer<NewRegistrationDTO>{
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun serialize(topic: String?, data: NewRegistrationDTO?): ByteArray? {
        log.info("Serializing New Registration...")
        return objectMapper.writeValueAsBytes(
            data ?: throw SerializationException("Error when serializing New registration to ByteArray[]")
        )
    }

    override fun close() {}
}