package it.polito.wa2.group22.ticketcatalogservice.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentRes(
    @JsonProperty("orderId")
    val orderId: Long,

    /**
     *  STATUS VALUES
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */

    @JsonProperty("status")
    val status: Int
)