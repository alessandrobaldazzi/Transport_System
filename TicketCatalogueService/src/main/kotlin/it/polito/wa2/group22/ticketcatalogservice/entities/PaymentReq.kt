package it.polito.wa2.group22.ticketcatalogservice.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentReq(
    @JsonProperty("orderId")
    val orderId: Long,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("creditCardNumber")
    val creditCardNumber: String,

    @JsonProperty("cvv")
    val cvv: String,

    @JsonProperty("expirationDate")
    val expirationDate: String,

    @JsonProperty("amount")
    val amount: Int
)