package it.polito.wa2.group22.authservice.dto

import com.fasterxml.jackson.annotation.JsonProperty

class NewRegistrationDTO (
    @JsonProperty("username")
    val username: String,
)