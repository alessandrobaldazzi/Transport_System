package it.polito.wa2.group22.ticketcatalogservice.kafka

class Topics {
    companion object Constants {
        const val ticketCatalogueToPayment: String = "ticketCatalogueToPayment"
        const val paymentToTicketCatalogue: String = "paymentToTicketCatalogue"
        const val successfulOrder: String = "successfulOrder"
    }
}
