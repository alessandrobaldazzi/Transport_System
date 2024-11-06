package it.polito.wa2.group22.travelerservice.dto

import it.polito.wa2.group22.travelerservice.entities.TicketPurchased
import java.sql.Timestamp

data class TicketPurchasedDTO(
    val sub: Long,
    var iat: String,
    val validfrom: String,
    var exp: String,
    var zid: String,
    val type: String,
    val jws: String
)

fun TicketPurchased.toDTO(): TicketPurchasedDTO{
    return TicketPurchasedDTO(
        this.id!!,
        this.iat.toString(),
        this.validFrom.toString(),
        this.exp.toString(),
        this.zid,
        this.type,
        this.jws
        )
}