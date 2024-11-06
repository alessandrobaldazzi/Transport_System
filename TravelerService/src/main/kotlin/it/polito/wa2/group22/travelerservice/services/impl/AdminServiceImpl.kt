package it.polito.wa2.group22.travelerservice.services.impl

import it.polito.wa2.group22.travelerservice.dto.TicketPurchasedDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceNotFoundException
import it.polito.wa2.group22.travelerservice.interfaces.*
import it.polito.wa2.group22.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.group22.travelerservice.services.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminServiceImpl: AdminService {

    @Autowired
    lateinit var userRepo: UserDetailsRepository

    @Autowired
    lateinit var ticketRepo: TicketPurchasedRepository


    override fun getTravelerProfile(username: String): AdminServiceGetProfileResponse {
        val profile = userRepo.findByUsername(username)
        if (profile != null) {
            return AdminServiceGetProfileResponse(
                UserProfileDTO(
                    profile.username,
                    profile.name,
                    profile.address,
                    profile.telephone,
                    profile.dateOfBirth
                )
            )
        } else {
            throw TravelerServiceNotFoundException("User not found")
        }
    }

    override fun getTravelerTickets(username: String): AdminServiceGetTravelerTicketResponse {
        val user = userRepo.findByUsername(username)
        return if (user != null) {
            AdminServiceGetTravelerTicketResponse(
                ticketRepo.getAllTicketsByUserProfile(UserProfile(user.username))
                    .map {
                        TicketPurchasedDTO(it.id!!, it.iat.toString(), it.validFrom.toString(), it.exp.toString(), it.zid, it.type, it.jws)
                    })
        } else {
            throw TravelerServiceNotFoundException("User not found")
        }
    }

    override fun getTravelers(): AdminServiceGetTravelersResponse {
        return AdminServiceGetTravelersResponse(
            userRepo.findAll().map {
                UserProfileDTO(it.username, it.name, it.address, it.telephone, it.dateOfBirth)
            }
        )
    }
}