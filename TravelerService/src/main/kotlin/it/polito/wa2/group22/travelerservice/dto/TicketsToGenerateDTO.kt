package it.polito.wa2.group22.travelerservice.dto

data class TicketsToGenerateDTO(
    val type: String,
    val duration: Int?,
    val zones: String,
    val quantity: Int,
    val username: String,
    val only_weekends: Boolean
)