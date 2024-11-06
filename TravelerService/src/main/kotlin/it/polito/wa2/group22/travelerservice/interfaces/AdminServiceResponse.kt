package it.polito.wa2.group22.travelerservice.interfaces

import it.polito.wa2.group22.travelerservice.dto.TicketPurchasedDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO

interface AdminServiceResponse {
}

data class AdminServiceGetProfileResponse(
    val user: UserProfileDTO
): AdminServiceResponse

data class AdminServiceGetTravelerTicketResponse(
    val tickets: List<TicketPurchasedDTO>
): AdminServiceResponse

data class AdminServiceGetTravelersResponse(
    val users: List<UserProfileDTO>
): AdminServiceResponse

