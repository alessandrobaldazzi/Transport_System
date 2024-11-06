package it.polito.wa2.group22.paymentservice.requests

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.UUID

data class BankRequest (

    @JsonProperty("paymentId")
    val paymentId: Long,

    @JsonProperty("creditCardNumber")
    val creditCardNumber: String,

    @JsonProperty("cvv")
    val cvv: String,

    @JsonProperty("expirationDate")
    val expirationDate: String,

    @JsonProperty("amount")
    val amount: BigDecimal

)