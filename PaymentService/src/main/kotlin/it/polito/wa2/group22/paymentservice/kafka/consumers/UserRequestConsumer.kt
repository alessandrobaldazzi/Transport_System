package it.polito.wa2.group22.paymentservice.kafka.consumers

import it.polito.wa2.group22.paymentservice.entities.Payment
import it.polito.wa2.group22.paymentservice.kafka.Topics
import it.polito.wa2.group22.paymentservice.repositories.PaymentRepository
import it.polito.wa2.group22.paymentservice.requests.BankRequest
import it.polito.wa2.group22.paymentservice.requests.UserRequest
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class UserRequestConsumer(
    @Value(Topics.paymentToBank) val topic: String,
    @Autowired
    @Qualifier("bankRequestTemplate")
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    @Autowired
    lateinit var paymentRepository: PaymentRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        containerFactory = "paymentRequestListenerContainerFactory",
        topics = [Topics.travelerToPayment],
        groupId="ppr"
    )
    fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<String, UserRequest>){
        logger.info("Incoming payment request {}", consumerRecord)

        val request = consumerRecord.value()

        val payment = Payment(null, request.orderId, request.username, 0, request.amount, LocalDateTime.now())

        runBlocking {
            val savedPayment = paymentRepository.save(payment)

            val bankRequest = BankRequest(
                savedPayment.payment!!,
                request.creditCardNumber,
                request.cvv,
                request.expirationDate,
                request.amount
            )

            logger.info("Sending payment request to bank")
            //logger.info("The message from kafka {}", bankRequest)

            val message: Message<BankRequest> =
                MessageBuilder
                    .withPayload(bankRequest)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .build()
            kafkaTemplate.send(message)
            logger.info("Message sent with success")
        }
    }
}