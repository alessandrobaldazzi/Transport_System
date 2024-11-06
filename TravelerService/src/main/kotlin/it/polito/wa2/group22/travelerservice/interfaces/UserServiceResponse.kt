package it.polito.wa2.group22.travelerservice.interfaces

import it.polito.wa2.group22.travelerservice.dto.QRCodeDTO
import it.polito.wa2.group22.travelerservice.dto.TicketPurchasedDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO

interface UserServiceResponse {
}
data class UserServiceGetProfileResponse(
    val userDetails: UserProfileDTO? = null,
) : UserServiceResponse

data class UserServiceUpdateProfileResponse(
    val result: Boolean? = null
) : UserServiceResponse

data class UserServiceGetTicketsResponse(
    val tickets: List<TicketPurchasedDTO>,
) : UserServiceResponse

data class UserServiceGetQRCodeResponse(
    val ticket: QRCodeDTO,
) : UserServiceResponse

