package it.polito.wa2.group22.travelerservice.services

import it.polito.wa2.group22.travelerservice.dto.TicketPurchasedDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetProfileResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetTravelerTicketResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetTravelersResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceResponse
import it.polito.wa2.group22.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import org.springframework.stereotype.Service

interface AdminService {
    fun getTravelerProfile(username: String): AdminServiceGetProfileResponse
    fun getTravelerTickets(username: String): AdminServiceGetTravelerTicketResponse
    fun getTravelers(): AdminServiceGetTravelersResponse
}
