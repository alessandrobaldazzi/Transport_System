package it.polito.wa2.group22.reportservice.config

import it.polito.wa2.group22.reportservice.message.SuccessfulTransitDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@EnableKafka
@Configuration
class KafkaSuccessfulTransitConsumerConfig(
    @Value("\${kafka.bootstrapAddress}") private val servers: String
) {

    @Bean
    fun successfulTransitConsumerFactory(): ConsumerFactory<String?, Any?> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "ppr"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = SuccessfulTransitDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun successfulTransitKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any>? {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = successfulTransitConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        factory.containerProperties.isSyncCommits = true
        return factory
    }
}