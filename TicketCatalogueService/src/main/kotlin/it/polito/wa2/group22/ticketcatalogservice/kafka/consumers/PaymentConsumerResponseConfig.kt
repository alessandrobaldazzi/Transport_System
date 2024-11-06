package it.polito.wa2.group22.ticketcatalogservice.kafka.consumers

import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentRes
import it.polito.wa2.group22.ticketcatalogservice.kafka.serializers.PaymentResponseDeserializer
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
class PaymentConsumerResponseConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val server: String
    ) {

    @Bean
    fun paymentResponseConsumerFactory(): ConsumerFactory<String?, PaymentRes> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = server
        props[ConsumerConfig.GROUP_ID_CONFIG] = "ctl"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = PaymentResponseDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun paymentResponseListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, PaymentRes> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, PaymentRes>()
        factory.consumerFactory = paymentResponseConsumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        factory.containerProperties.isSyncCommits = true
        return factory
    }

}