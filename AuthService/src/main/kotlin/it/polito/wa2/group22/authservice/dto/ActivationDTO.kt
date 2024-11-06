package it.polito.wa2.group22.authservice.dto

import it.polito.wa2.group22.authservice.entities.Activation
import java.util.UUID
import java.util.Date

data class ActivationDTO(
    var email: String?,
    var activation_code: String?,
    var provisional_id: UUID,
    var expDate: Date?,
) {

}

fun Activation.toDTO(): ActivationDTO {
    return ActivationDTO(this.user.email, this.activationCode, this.id!!, this.expDate)
}