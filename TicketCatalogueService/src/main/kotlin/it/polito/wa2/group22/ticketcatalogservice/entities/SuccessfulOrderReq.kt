package it.polito.wa2.group22.ticketcatalogservice.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class SuccessfulOrderReq (
    @JsonProperty("order_id") val order_id: Long?,
    @JsonProperty("ticket_type") val ticket_type: String,
    @JsonProperty("quantity") val quantity: Int,
    @JsonProperty("username") val username: String
)