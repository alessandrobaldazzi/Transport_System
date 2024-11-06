package it.polito.wa2.group22.fakebankservice.kafka

class Topics {
    companion object Constants {
        const val paymentToBank: String = "paymentToBank"
        const val bankToPayment: String = "bankToPayment"
    }
}