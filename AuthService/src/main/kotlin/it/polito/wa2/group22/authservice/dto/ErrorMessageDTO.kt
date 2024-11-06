package it.polito.wa2.group22.authservice.dto

data class ErrorMessageDTO(
    var status: Int? = null,
    var message: String? = null
)