package it.polito.wa2.group22.paymentservice.controllers

import it.polito.wa2.group22.paymentservice.entities.Payment
import it.polito.wa2.group22.paymentservice.services.PaymentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.context.ReactiveSecurityContextHolder

@RestController
class PaymentController {

    @Autowired
    lateinit var paymentService: PaymentService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as String }

    /**
    *Get /admin/transactions → get transactions of all users*
    **/

    @GetMapping("/admin/transactions")
    fun getTransactionsUsers() : Flow<Payment>{
        //Add admin validation
        return paymentService.getAllTransactions()
    }


    /**
     * Get /transactions → get transactions of the current user
     * **/

    @GetMapping("/transactions")
    suspend fun getTransactionsCurrentUser():Flow<Payment>{
        val userId = principal.awaitSingle()
        return paymentService.getUserTransactions(userId)
    }

}