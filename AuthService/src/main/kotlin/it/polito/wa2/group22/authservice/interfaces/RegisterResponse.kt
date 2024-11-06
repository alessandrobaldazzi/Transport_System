package it.polito.wa2.group22.authservice.interfaces

import it.polito.wa2.group22.authservice.dto.UserAdminDTO
import it.polito.wa2.group22.authservice.utils.RegistrationResult
import java.util.*

interface RegisterResponse
data class RegisterResponseValid(
    val provisional_id: UUID,
    val email: String
) : RegisterResponse

data class RegisterResponseError(
    val errorType: RegistrationResult
) : RegisterResponse

data class RegisterResponseAdmin(
    val errorType: RegistrationResult,
    val userAdminDTO: UserAdminDTO
) : RegisterResponse