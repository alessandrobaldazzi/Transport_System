package it.polito.wa2.group22.ticketcatalogservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class TicketCatalogServiceApplication

fun main(args: Array<String>) {
    runApplication<TicketCatalogServiceApplication>(*args)
}
