package it.polito.wa2.group22.paymentservice.services

import it.polito.wa2.group22.paymentservice.entities.Payment
import it.polito.wa2.group22.paymentservice.repositories.PaymentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlinx.coroutines.flow.Flow

@Service
class PaymentService {

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    fun getAllTransactions() : Flow<Payment>{
        return paymentRepository.findAll()
    }

     suspend fun getUserTransactions(userId : String) : Flow<Payment>{
        return paymentRepository.findAllByUserId(userId)
    }
}