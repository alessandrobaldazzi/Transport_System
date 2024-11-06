package it.polito.wa2.group22.reportservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
class Order(
    @Column var ticket_type: String,
    @Column var quantity: Int,
    @Column var username: String
) {
    @Id var id: Long? = null
}