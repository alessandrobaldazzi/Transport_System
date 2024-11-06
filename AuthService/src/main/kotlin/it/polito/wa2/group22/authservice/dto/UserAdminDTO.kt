package it.polito.wa2.group22.authservice.dto

import it.polito.wa2.group22.authservice.utils.RoleName

data class UserAdminDTO(
    val username: String,
    var password: String,
    var email: String,
    val role : RoleName,
)
