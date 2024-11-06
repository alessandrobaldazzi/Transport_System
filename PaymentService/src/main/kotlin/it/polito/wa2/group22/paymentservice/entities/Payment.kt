package it.polito.wa2.group22.paymentservice.entities

import it.polito.wa2.group22.paymentservice.dtos.PaymentDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class Payment(
    @Id
    @Column("paymentid")
    val payment : Long?,

    @Column("orderid")
    val orderID:Long,

    @Column("userid")
    val userId : String,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */

    @Column("status")
    var status: Int,

    @Column var amount: BigDecimal,

    @Column("issuedat")
    var issuedAt: LocalDateTime,

    )

fun Payment.toDTO() = PaymentDTO(payment!!, amount, userId, issuedAt, orderID, status)