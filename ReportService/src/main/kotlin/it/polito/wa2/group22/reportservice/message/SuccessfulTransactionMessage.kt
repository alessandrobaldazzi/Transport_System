package it.polito.wa2.group22.reportservice.message

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SuccessfulTransactionMessage(
    @JsonProperty("transaction_id") val transaction_id: Long?,
    @JsonProperty("amount") val amount: BigDecimal,
    @JsonProperty("username") val username: String,
    @JsonProperty("issued_at") val issued_at: String
)