package it.polito.wa2.group22.travelerservice.dto

import it.polito.wa2.group22.travelerservice.entities.UserProfile
import java.util.Date

data class UserProfileDTO(
    val username: String,
    val name: String?,
    val address: String?,
    val telephone: String?,
    val dateOfBirth: String?,
)

fun UserProfile.toDTO(): UserProfileDTO {
    return UserProfileDTO(
        this.username,
        this.name,
        this.address,
        this.telephone,
        this.dateOfBirth
    )
}