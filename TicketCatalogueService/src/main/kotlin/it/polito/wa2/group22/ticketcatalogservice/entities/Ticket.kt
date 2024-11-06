package it.polito.wa2.group22.ticketcatalogservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Duration

@Table("tickets")
data class Ticket(
    @Id
    var id: Long,
    var name: String,
    var price: Float,
    var duration: Int?,
    var zones: String,
    var type: String,
    var max_age: Int?,
    var min_age: Int?
)

