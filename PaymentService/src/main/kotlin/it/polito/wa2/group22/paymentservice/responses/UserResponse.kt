package it.polito.wa2.group22.paymentservice.responses

import com.fasterxml.jackson.annotation.JsonProperty

class UserResponse (

    @JsonProperty("orderId")
    val orderId: Long,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */
    @JsonProperty("status")
    val status: Int

)