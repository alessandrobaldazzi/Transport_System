package it.polito.wa2.group22.fakebankservice.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentRequest(

    @JsonProperty("paymentId")
    val paymentId: Long,

    @JsonProperty("creditCardNumber")
    val creditCardNumber: String,

    @JsonProperty("cvv")
    val cvv: String,

    @JsonProperty("expirationDate")
    val expirationDate: String,

    @JsonProperty("amount")
    val amount: Int

)