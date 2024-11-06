package it.polito.wa2.group22.paymentservice.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

class BankResponse (
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