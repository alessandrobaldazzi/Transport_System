package it.polito.wa2.group22.authservice.utils

enum class ActivationResult {
    SUCCESS,
    EXPIRED,
    ID_NOT_EXIST,
    WRONG_CODE
}

val actResultToMessage = mapOf<ActivationResult, String>(
    ActivationResult.SUCCESS to "User successfully activated",
    ActivationResult.EXPIRED to "Activation code expired",
    ActivationResult.WRONG_CODE to "Wrong activation code",
    ActivationResult.ID_NOT_EXIST to "Activation ID does not exist"
)