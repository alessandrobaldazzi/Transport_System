package it.polito.wa2.group22.ticketcatalogservice.dtos

data class PurchaseStatsDTO (
    val percOrdinaryTickets: Long,
    val percTravelerCards: Long,
    val ticketsNumber: Int,
)