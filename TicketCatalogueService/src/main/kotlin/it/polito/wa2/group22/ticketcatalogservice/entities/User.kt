package it.polito.wa2.group22.ticketcatalogservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    val username: String,
    val email: String
)