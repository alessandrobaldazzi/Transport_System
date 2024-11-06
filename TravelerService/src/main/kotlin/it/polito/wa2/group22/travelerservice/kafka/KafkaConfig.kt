package it.polito.wa2.group22.travelerservice.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin


@Configuration
class KafkaConfig(@Value("\${spring.kafka.bootstrap-servers}") private val server: String) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        return KafkaAdmin(configs)
    }

    /** incoming new registration */
    @Bean
    fun authToTraveler(): NewTopic {
        return NewTopic(Topics.authToTraveler, 1, 1.toShort())
    }
}