package it.polito.wa2.group22.ticketcatalogservice.controllers

import it.polito.wa2.group22.ticketcatalogservice.dtos.BuyTicketPaymentDTO
import it.polito.wa2.group22.ticketcatalogservice.dtos.UserProfileDTO
import it.polito.wa2.group22.ticketcatalogservice.entities.Order
import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import it.polito.wa2.group22.ticketcatalogservice.services.TicketCatalogueService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RestController
class TicketCatalogueController(@Value("\${traveler-service-endpoint}") travelerServiceEndpoint: String) {

    private val webClient: WebClient = WebClient.create(travelerServiceEndpoint)

    @Autowired
    lateinit var ticketCatalogueService: TicketCatalogueService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as String }

    @GetMapping("/tickets")
    suspend fun getAllTickets(): Flow<Ticket> {
        return ticketCatalogueService.getAllTickets()
    }

    @GetMapping("/orders")
    suspend fun getAllUserOrders(): Flow<Order> {
        val username = principal.awaitSingle()
        return ticketCatalogueService.getOrdersByUser(username)
    }

    @GetMapping("/orders/{orderId}")
    suspend fun getOrderById(@PathVariable orderId: Long): Order? {
        val username = principal.awaitSingle()
        return ticketCatalogueService.getOrderById(orderId, username)
    }

    @GetMapping("/admin/orders")
    fun getAllOrders(): Flow<Order> {
        return ticketCatalogueService.getAllOrders()
    }

    @GetMapping("/admin/orders/{username}")
    suspend fun getAllUserOrdersAdmin(@PathVariable username: String): Flow<Order> {
        return ticketCatalogueService.getOrdersByUser(username)
    }

    @PostMapping("/admin/tickets")
    suspend fun addNewTicket(@RequestBody newTicket: Ticket): Flow<Ticket> {
        return ticketCatalogueService.createNewTicket(newTicket)
    }

    @PutMapping("/admin/tickets/{ticketId}")
    suspend fun updateTicket(@PathVariable ticketId: Long, @RequestBody ticket: Ticket): ResponseEntity<Ticket?> {
        val newTicket = ticketCatalogueService.updateTicket(ticketId, ticket)
        return if (newTicket == null) ResponseEntity(null, HttpStatus.UNPROCESSABLE_ENTITY)
        else return ResponseEntity(newTicket, HttpStatus.OK)
    }

    @PostMapping("/shop/{ticketId}")
    suspend fun buyTicket(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable ticketId: Long,
        @RequestBody paymentBuyInfo: BuyTicketPaymentDTO
    ): ResponseEntity<Order> {
        //val tmp = paymentBuyInfo as BuyTicketPaymentDTO
        val userProfileDTO: UserProfileDTO?
        try {
            userProfileDTO = webClient
                .get()
                .uri("/v1/profile")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody()
        } catch (e: Exception) {
            println(e.printStackTrace())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        val username = principal.awaitSingle()

        val ticket = ticketCatalogueService.getTicketById(ticketId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        // Check if the user is eligible
        val dateOfBirth = LocalDate.parse(userProfileDTO.dateOfBirth, DateTimeFormatter.ISO_DATE)
        val age = (Period.between(dateOfBirth, LocalDate.now()).toTotalMonths()) / 12
        if (
            ((ticket.max_age != null) && (ticket.max_age!! < age.toInt())) || (((ticket.min_age != null) && (ticket.min_age!! > age.toInt())))
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val order = ticketCatalogueService.buyTicket(
            username, ticketId, paymentBuyInfo
        )
        return ResponseEntity.status(HttpStatus.OK).body(order)
    }
}