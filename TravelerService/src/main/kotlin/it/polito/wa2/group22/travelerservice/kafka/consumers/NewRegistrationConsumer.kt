package it.polito.wa2.group22.travelerservice.kafka.consumers

import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.kafka.Topics
import it.polito.wa2.group22.travelerservice.kafka.dto.NewRegistrationDTO
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.group22.travelerservice.services.UserService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class NewRegistrationConsumer(
    @Value(Topics.authToTraveler) val topic: String
) {

    @Autowired
    lateinit var userRepo: UserDetailsRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        containerFactory = "newRegistrationListenerContainerFactory",
        topics = [Topics.authToTraveler],
        groupId = "ppr"
    )
    fun listenFromAuthService(consumerRecord: ConsumerRecord<Any, NewRegistrationDTO>) {

        /** receive from AuthService... */
        logger.info("Incoming new registration response {}", consumerRecord)

        val response = consumerRecord.value()

        userRepo.save(UserProfile(response.username))
    }

    /*fun forwardPaymentResponse(response:UserResponse) {

        val message: Message<UserResponse> = MessageBuilder
            .withPayload(response)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")
    }*/

}