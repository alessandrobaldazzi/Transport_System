package it.polito.wa2.group22.paymentservice.requests

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

class UserRequest (
    @JsonProperty("orderId")
    val orderId: Long,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("creditCardNumber")
    val creditCardNumber: String,

    @JsonProperty("cvv")
    val cvv: String,

    @JsonProperty("expirationDate")
    val expirationDate: String,

    @JsonProperty("amount")
    val amount: BigDecimal
)