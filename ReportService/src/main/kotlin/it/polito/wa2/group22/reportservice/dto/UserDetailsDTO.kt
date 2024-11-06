package it.polito.wa2.group22.reportservice.dto

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserDetailsDTO(
    val userName: String,
    var roles: MutableList<SimpleGrantedAuthority>?
)