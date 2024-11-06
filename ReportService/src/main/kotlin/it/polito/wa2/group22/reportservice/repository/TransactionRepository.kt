package it.polito.wa2.group22.reportservice.repository

import it.polito.wa2.group22.reportservice.entity.Transaction
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface TransactionRepository : CoroutineCrudRepository<Transaction, Long> {
}