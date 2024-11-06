package it.polito.wa2.group22.travelerservice.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_profile")
class UserProfile(

    @Id
    var username: String = "",

    var name: String? = null,
    var address: String? = null,
    var telephone: String? = null,
    var dateOfBirth : String? = null,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userProfile")
    var tickets: MutableList<TicketPurchased> = mutableListOf()
)


