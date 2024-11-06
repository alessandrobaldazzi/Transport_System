package it.polito.wa2.group22.travelerservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class TravelerServiceApplication

fun main(args: Array<String>) {
	runApplication<TravelerServiceApplication>(*args)
}
