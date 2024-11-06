package it.polito.wa2.group22.paymentservice.dtos

import java.math.BigDecimal
import java.time.LocalDateTime

class PaymentDTO (
    val id: Long,
    val amount: BigDecimal,
    val username: String,
    val issuedAt: LocalDateTime,
    val orderId: Long,
    val status: Int
    )