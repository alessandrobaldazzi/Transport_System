package it.polito.wa2.group22.reportservice.repository

import it.polito.wa2.group22.reportservice.entity.Transit
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TransitRepository : CoroutineCrudRepository<Transit, Long> {
}