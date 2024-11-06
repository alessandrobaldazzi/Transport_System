package it.polito.wa2.group22.paymentservice.kafka.consumers

import it.polito.wa2.group22.paymentservice.kafka.Topics
import it.polito.wa2.group22.paymentservice.repositories.PaymentRepository
import it.polito.wa2.group22.paymentservice.responses.BankResponse
import it.polito.wa2.group22.paymentservice.responses.UserResponse
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

@Component
class PaymentResponseConsumer(
    @Value(Topics.paymentToTraveler) val topic: String,
    @Autowired
    @Qualifier("userResponseTemplate")
    private val kafkaTemplate: KafkaTemplate<String,UserResponse>
) {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        containerFactory = "paymentResponseListenerContainerFactory",
        topics = [Topics.bankToPayment],
        groupId = "ppr"
    )
    fun listenFromBank(consumerRecord: ConsumerRecord<Any, BankResponse>) {

        /** receive from Bank... */
        logger.info("Incoming payment response {}", consumerRecord)

        val response = consumerRecord.value()


        runBlocking {

            // update DB
            val payment = paymentRepository.findById(response.paymentId)
            if (payment != null) {
                payment.status = response.status
                paymentRepository.save(payment)
            }

            // Forward the response to catalogue service
            val paymentResponse = payment?.let {
                UserResponse(
                    it.orderID,
                    response.status
                )
            }
            logger.info("Sending payment response out..")
            logger.info("The message to Kafka: {}", consumerRecord.value())

            if (paymentResponse != null) {
                forwardPaymentResponse(paymentResponse)
            }
        }


    }

    fun forwardPaymentResponse(response:UserResponse) {

        val message: Message<UserResponse> = MessageBuilder
            .withPayload(response)
            .setHeader(KafkaHeaders.TOPIC, topic)
            .build()
        kafkaTemplate.send(message)
        logger.info("Message sent with success")
    }

}
