package it.polito.wa2.group22.ticketcatalogservice.repositories

import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : CoroutineCrudRepository<Ticket, Long> {
    suspend fun findTicketById(id: Long): Ticket?
}