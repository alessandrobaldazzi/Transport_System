package it.polito.wa2.group22.authservice.interfaces

import it.polito.wa2.group22.authservice.utils.ValidationResult

interface ValidationResponse
data class ValidationResponseValid(
    val userId: Long,
    val username: String,
    val email: String
) : ValidationResponse

data class ValidationResponseError(
    val errorType: ValidationResult
) : ValidationResponse