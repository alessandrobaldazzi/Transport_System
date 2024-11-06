package it.polito.wa2.group22.ticketcatalogservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    @Id
    val id: Long?,

    @Column("ticketid")
    val ticketId: Long,

    val quantity: Int,

    @Column("username")
    val username: String,

    @Column("status")
    var status: String,

    @org.springframework.data.annotation.Transient
    val ticket: Ticket?,

    @org.springframework.data.annotation.Transient
    val user: User?,
)