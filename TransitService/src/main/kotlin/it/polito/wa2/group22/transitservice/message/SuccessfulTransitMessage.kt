package it.polito.wa2.group22.transitservice.message

import com.fasterxml.jackson.annotation.JsonProperty

data class SuccessfulTransitMessage(
    @JsonProperty("transit_id") val transit_id: Long?,
    @JsonProperty("ticket_type") val ticket_type: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("transit_date") val transit_date: String,
)