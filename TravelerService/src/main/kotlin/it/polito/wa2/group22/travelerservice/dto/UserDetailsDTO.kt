    package it.polito.wa2.group22.travelerservice.dto

import it.polito.wa2.group22.travelerservice.entities.UserProfile
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserDetailsDTO(
    var username: String,
    var roles: MutableList<SimpleGrantedAuthority>?
)

/*fun UserProfile.toDTO(): UserDetailsDTO{
    return UserDetailsDTO(username, address, date_of_birth, telephone_number, null)
}*/