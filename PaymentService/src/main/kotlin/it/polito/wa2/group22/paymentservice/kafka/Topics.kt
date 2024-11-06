package it.polito.wa2.group22.paymentservice.kafka

class Topics {
    companion object Constants {
        const val paymentToBank: String = "paymentToBank"
        const val bankToPayment: String = "bankToPayment"
        //const val travelerToPayment: String = "travelerToPayment"
        const val travelerToPayment: String = "ticketCatalogueToPayment"
        const val paymentToTraveler: String = "paymentToTicketCatalogue"
    }
}