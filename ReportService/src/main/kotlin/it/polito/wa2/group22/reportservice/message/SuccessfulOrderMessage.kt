package it.polito.wa2.group22.reportservice.message

import com.fasterxml.jackson.annotation.JsonProperty

data class SuccessfulOrderMessage(
    @JsonProperty("order_id") val order_id: Long?,
    @JsonProperty("ticket_type") val ticket_type: String,
    @JsonProperty("quantity") val quantity: Int,
    @JsonProperty("username") val username: String
)