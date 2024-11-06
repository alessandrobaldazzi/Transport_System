package it.polito.wa2.group22.authservice.utils

enum class EmailResult {
    SUCCESS,
    MISSING_USERNAME,
    MISSING_ACT_CODE,
    MISSING_EMAIL
}

val emailResultToMessage = mapOf<EmailResult, String>(
    EmailResult.SUCCESS to "User successfully activated",
    EmailResult.MISSING_USERNAME to "Activation code expired",
    EmailResult.MISSING_ACT_CODE to "Wrong activation code",
    EmailResult.MISSING_EMAIL to "Activation ID does not exist"
)