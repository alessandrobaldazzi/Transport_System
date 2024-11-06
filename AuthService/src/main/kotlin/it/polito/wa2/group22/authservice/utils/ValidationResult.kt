package it.polito.wa2.group22.authservice.utils

enum class ValidationResult {
    VALID_VALIDATION,
    EXPIRED_VALIDATION,
    NOT_VALID_ID,
    NOT_VALID_ACTIVATION_CODE,
    INVALID_REQUEST,
    LIMIT_ATTEMPT
}