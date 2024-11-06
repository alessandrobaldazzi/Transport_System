package it.polito.wa2.group22.ticketcatalogservice.services

import it.polito.wa2.group22.ticketcatalogservice.repositories.OrderRepository
import it.polito.wa2.group22.ticketcatalogservice.repositories.TicketRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import it.polito.wa2.group22.ticketcatalogservice.dtos.PurchaseStatsDTO
import it.polito.wa2.group22.ticketcatalogservice.enums.TicketType

@Service
class ReportService {
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    suspend fun getOrdersInfo(orderIDs: List<Int>, jwt: String): PurchaseStatsDTO {
        val tickets = orderIDs
            .map { orderRepository.findOrderById(it.toLong())!! }
            .map { ticketRepository.findTicketById(it.ticketId)!! }
        return if(tickets.isEmpty())
            PurchaseStatsDTO(
                -1,
                -1,
                orderIDs.map { orderRepository.findOrderById(it.toLong())!! }.sumOf { it.quantity }
            )
        else{
            PurchaseStatsDTO(
                (100*tickets.count { it.type == TicketType.values().find { type -> type == TicketType.ORDINARY }.toString() }.toLong() / tickets.count().toLong()),
                (100*tickets.count { it.type == TicketType.values().find { type -> type != TicketType.ORDINARY }.toString() }.toLong() / tickets.count().toLong()),
                orderIDs.map { orderRepository.findOrderById(it.toLong())!! }.sumOf { it.quantity }
            )
        }
    }
}