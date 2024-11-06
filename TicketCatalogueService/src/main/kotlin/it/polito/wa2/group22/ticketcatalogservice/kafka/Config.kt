package it.polito.wa2.group22.ticketcatalogservice.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class Config(
    @Value("\${spring.kafka.bootstrap-servers}") private val server: String
    ) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        return KafkaAdmin(configs)
    }

    @Bean
    fun paymentToTraveler(): NewTopic {
        return NewTopic(Topics.paymentToTicketCatalogue, 1, 1.toShort())
    }

    @Bean
    fun travelerToPayment(): NewTopic {
        return NewTopic(Topics.ticketCatalogueToPayment, 1, 1.toShort())
    }

}