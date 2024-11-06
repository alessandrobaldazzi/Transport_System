package it.polito.wa2.group22.authservice.utils

enum class RegistrationResult {
    VALID_USER,
    BLANK_EMAIL,
    BLANK_USERNAME,
    BLANK_PASSWORD,
    USERNAME_IS_NOT_UNIQUE,
    EMAIL_IS_NOT_UNIQUE,
    INVALID_PASSWORD,
    INVALID_EMAIL,
    DB_ERROR
}