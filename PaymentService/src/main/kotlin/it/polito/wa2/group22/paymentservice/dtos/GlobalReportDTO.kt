package it.polito.wa2.group22.paymentservice.dtos

data class GlobalReportDTO(
    val purchases : Int,
    val profit : Float,
    val transits : Int,
    val avarageProfit: Float,
    val percClassicTickets: Float,
    val percTravelersCards: Float,
    val percTransitsClassicTickets: Float,
    val percTransitsTravelerCards: Float,
    val ticketsNumber: Int
)