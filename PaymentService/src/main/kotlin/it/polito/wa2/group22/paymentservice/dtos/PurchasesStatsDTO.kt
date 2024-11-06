package it.polito.wa2.group22.paymentservice.dtos

data class PurchasesStatsDTO(
    val percOrdinaryTickets: Long,
    val percTravelerCards: Long,
    val ticketsNumber: Int,
)