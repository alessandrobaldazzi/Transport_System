package it.polito.wa2.group22.ticketcatalogservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("payments")
data class PaymentInfo(
    val creditCardNumber: Int,
    val cvv: Int,
    val expirationDate: String
)