package it.polito.wa2.group22.paymentservice.repositories

import it.polito.wa2.group22.paymentservice.entities.Payment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Repository
interface PaymentRepository : CoroutineCrudRepository<Payment, Long> {

    suspend fun findAllByUserId(userId: String): Flow<Payment>

}