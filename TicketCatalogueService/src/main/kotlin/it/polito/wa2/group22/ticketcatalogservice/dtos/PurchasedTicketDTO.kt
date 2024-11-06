package it.polito.wa2.group22.ticketcatalogservice.dtos

data class PurchasedTicketDTO (
    val sub: Long,
    val iat: String,
    val validfrom: String,
    val exp: String,
    val zid: String,
    val type: String,
    val jws: String
)