package it.polito.wa2.group22.fakebankservice.entities


import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentResponse(

    @JsonProperty("paymentId")
    val paymentId: Long,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */
    @JsonProperty("status")
    val status: Int

)