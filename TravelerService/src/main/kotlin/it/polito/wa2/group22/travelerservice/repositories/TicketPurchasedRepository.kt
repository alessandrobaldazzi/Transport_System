package it.polito.wa2.group22.travelerservice.repositories

import it.polito.wa2.group22.travelerservice.entities.TicketPurchased
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketPurchasedRepository : CrudRepository<TicketPurchased, String>{

    //@Query("SELECT t FROM TicketPurchased AS t WHERE t.userDetails = ?1")
    fun getAllTicketsByUserProfile(userProfile: UserProfile) : List<TicketPurchased>

    fun getTicketPurchasedById(id: Long): TicketPurchased

}