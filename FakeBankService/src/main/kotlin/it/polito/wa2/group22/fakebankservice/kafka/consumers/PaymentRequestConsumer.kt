package it.polito.wa2.group22.fakebankservice.kafka.consumers

import it.polito.wa2.group22.fakebankservice.entities.PaymentRequest
import it.polito.wa2.group22.fakebankservice.entities.PaymentResponse
import it.polito.wa2.group22.fakebankservice.kafka.Topics
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
import kotlin.random.Random


@Component
class PaymentRequestConsumer(
    @Value(Topics.bankToPayment) val topic: String,
    @Autowired
    @Qualifier("paymentResponseTemplate")
    private val kafkaTemplate: KafkaTemplate<String, PaymentResponse>
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        containerFactory = "paymentRequestListenerContainerFactory",
        topics = [Topics.paymentToBank],
        groupId = "bnk"
    )
    fun listenFromTicketCatalogue(consumerRecord: ConsumerRecord<Any, Any>) {

        logger.info("Incoming payment request from PaymentService {}", consumerRecord)
        logger.info("Processing request...")

        // Simulating payment process
        Thread.sleep(4000)
        val paymentSucceeded = Random.nextInt(100) > 50

        val request = consumerRecord.value() as PaymentRequest

        // TODO randomize status
        val message: Message<PaymentResponse> = MessageBuilder
            .withPayload(PaymentResponse(request.paymentId, if (paymentSucceeded) 1 else 0))
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")

    }

}