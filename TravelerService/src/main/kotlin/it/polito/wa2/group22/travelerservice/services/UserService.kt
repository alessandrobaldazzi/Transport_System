package it.polito.wa2.group22.travelerservice.services

import it.polito.wa2.group22.travelerservice.dto.GeneratedTicketDTO
import it.polito.wa2.group22.travelerservice.dto.TicketPurchasedDTO
import it.polito.wa2.group22.travelerservice.dto.TicketsToGenerateDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.interfaces.*
import org.springframework.stereotype.Service

@Service
interface UserService{
    fun getUserProfile(userName: String): UserServiceGetProfileResponse
    fun updateUserProfile(userProfileDTO: UserProfileDTO, username: String): UserServiceUpdateProfileResponse
    fun getUserTickets(username: String): UserServiceGetTicketsResponse
    fun getQRCodeTicket(ticketId: Long): UserServiceGetQRCodeResponse
    fun generateTickets(ticketsToGenerate: TicketsToGenerateDTO): List<TicketPurchasedDTO>?
}