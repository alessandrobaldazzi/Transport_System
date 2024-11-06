package it.polito.wa2.group22.ticketcatalogservice.dtos

data class BuyTicketPaymentDTO(
    val amount: Int,
    val creditCardNumber: String,
    val cvv: String,
    val expirationDate: String
)
