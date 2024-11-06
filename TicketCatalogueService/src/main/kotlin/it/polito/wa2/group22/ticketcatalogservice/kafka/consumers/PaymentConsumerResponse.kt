package it.polito.wa2.group22.ticketcatalogservice.kafka.consumers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.group22.ticketcatalogservice.dtos.PurchasedTicketDTO
import it.polito.wa2.group22.ticketcatalogservice.dtos.TicketToBuyDTO
import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentRes
import it.polito.wa2.group22.ticketcatalogservice.entities.SuccessfulOrderReq
import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import it.polito.wa2.group22.ticketcatalogservice.kafka.Topics
import it.polito.wa2.group22.ticketcatalogservice.repositories.OrderRepository
import it.polito.wa2.group22.ticketcatalogservice.repositories.TicketRepository
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import it.polito.wa2.group22.ticketcatalogservice.security.JWTUtils


@Component
class PaymentConsumerResponse {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    @Qualifier("kafkaSuccessfulOrderTemplate")
    lateinit var kafkaSuccessfulOrderTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var ordersRepository: OrderRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    @Autowired
    lateinit var jwtUtils: JWTUtils


    @KafkaListener(
        containerFactory = "paymentResponseListenerContainerFactory",
        topics = [Topics.paymentToTicketCatalogue],
        groupId = "ctl"
    )
    fun listenFromPaymentService(consumerRecord: ConsumerRecord<Any, PaymentRes>) {

        logger.info("Incoming payment {}", consumerRecord)

        val response = consumerRecord.value()
        if (response.status != 1){
            runBlocking {
                val targetOrder = ordersRepository.findOrderById(response.orderId)
                //logger.error("Received payment response for non-existing order {}", response.orderId)
                targetOrder!!.status = "ERROR"
            }
        }else{
            val targetOrder = runBlocking { ordersRepository.findOrderById(response.orderId) }
            val targetTicket: Ticket? = runBlocking { ticketRepository.findById(targetOrder!!.ticketId) }
            val ticketToBuy = TicketToBuyDTO(
                targetTicket!!.type,
                targetTicket.duration,
                targetTicket.zones,
                targetOrder!!.quantity,
                targetOrder.username,
                false,
            )

            val mapper = jacksonObjectMapper()
            val body = mapper.writeValueAsString(ticketToBuy)

            val jwt = jwtUtils.generateToken(targetOrder.username, listOf("ROLE_TICKETCATALOGUESERVICE"))

            val ticketPurchased : List<PurchasedTicketDTO> = runBlocking {
                WebClient
                .create("http://localhost:8083")
                .post()
                .uri("v1/ticket/generate")
                .header("Authorization", "Bearer "+jwt)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .awaitBody()
            }

            targetOrder.status = "COMPLETED"
            runBlocking {  ordersRepository.save(targetOrder)}

            val successfulOrderMessage: Message<SuccessfulOrderReq> = MessageBuilder
                .withPayload(
                    SuccessfulOrderReq(
                        targetOrder.id,
                        targetTicket.type,
                        targetOrder.quantity,
                        targetOrder.username
                    )
                )
                .setHeader(KafkaHeaders.TOPIC, Topics.successfulOrder)
                .setHeader("X-Custom-Header", "Custom header here")
                .build()

            kafkaSuccessfulOrderTemplate.send(successfulOrderMessage)
        }


    }
}