package it.polito.wa2.group22.travelerservice.entities

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "ticket_purchased")
class TicketPurchased(
    @Column(nullable = false)
    var zid: String,

    @ManyToOne
    @JoinColumn(name = "user_details_username")
    var userProfile: UserProfile? = null
){
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    var iat: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    var validFrom: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

    @Column(nullable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    var exp: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.SECONDS))

    @Column(nullable = false)
    var type: String = "Ordinary"

    var jws: String = ""
}