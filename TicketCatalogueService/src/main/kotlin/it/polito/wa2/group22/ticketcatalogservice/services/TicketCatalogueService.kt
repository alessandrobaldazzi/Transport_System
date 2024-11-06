package it.polito.wa2.group22.ticketcatalogservice.services

import it.polito.wa2.group22.ticketcatalogservice.dtos.BuyTicketPaymentDTO
import it.polito.wa2.group22.ticketcatalogservice.entities.Order
import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentReq
import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import it.polito.wa2.group22.ticketcatalogservice.enums.TicketType
import it.polito.wa2.group22.ticketcatalogservice.kafka.Topics
import it.polito.wa2.group22.ticketcatalogservice.repositories.OrderRepository
import it.polito.wa2.group22.ticketcatalogservice.repositories.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class TicketCatalogueService(
    @Autowired
    @Qualifier("paymentRequestTemplate")
    private val paymentRequestTemplate: KafkaTemplate<String, Any>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    fun getAllOrders(): Flow<Order> {
        return orderRepository.findAllOrders()
    }

    fun getAllTickets(): Flow<Ticket> {
        return ticketRepository.findAll()
    }

    suspend fun createNewTicket(ticket: Ticket): Flow<Ticket> {
        val savedTicket = ticketRepository.save(ticket)
        return flow {
            emit(savedTicket)
        }
    }

    suspend fun getOrderById(orderId: Long, username: String): Order? {
        return orderRepository.findOrderByUsername(orderId, username)
    }

    fun getOrdersByUser(username: String): Flow<Order> {
        return orderRepository.findOrdersByUser(username)
    }

    suspend fun getTicketById(id: Long): Ticket? {
        return ticketRepository.findTicketById(id)
    }

    suspend fun buyTicket(username: String, ticketId: Long, buyTicketPaymentDTO: BuyTicketPaymentDTO) : Order {

        val order = orderRepository.save(
            Order(
                null, ticketId, buyTicketPaymentDTO.amount, username, "PENDING", null, null
            )
        )

        log.info("Receiving payment request")
        log.info("Sending payment message to Kafka {}", buyTicketPaymentDTO)
        val message: Message<PaymentReq> = MessageBuilder
            .withPayload(PaymentReq(
                order.id!!,
                username,
                buyTicketPaymentDTO.creditCardNumber,
                buyTicketPaymentDTO.cvv,
                buyTicketPaymentDTO.expirationDate,
                buyTicketPaymentDTO.amount,
            ))
            //.setHeader(KafkaHeaders.TOPIC, Topics.paymentToTicketCatalogue)
            .setHeader(KafkaHeaders.TOPIC, Topics.ticketCatalogueToPayment)
            .build()

        paymentRequestTemplate.send(message)
        log.info("Message sent with success")
        return order
    }

    suspend fun updateTicket(ticketId: Long, updatedTicket: Ticket): Ticket? {
        if (updatedTicket.id != null && updatedTicket.id != ticketId)
            return null
        if ((updatedTicket.type == TicketType.ORDINARY.toString() && updatedTicket.duration == null) ||
            (updatedTicket.type != TicketType.ORDINARY.toString() && updatedTicket.duration != null))
            return null

        val newTicket: Ticket = ticketRepository.findById(ticketId) ?: return null
        newTicket.name = updatedTicket.name
        newTicket.type = updatedTicket.type
        newTicket.duration = updatedTicket.duration
        newTicket.zones = updatedTicket.zones
        newTicket.price = updatedTicket.price
        newTicket.min_age = updatedTicket.min_age
        newTicket.max_age = updatedTicket.max_age
        ticketRepository.save(newTicket)

        return newTicket
    }
}