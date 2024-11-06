package it.polito.wa2.group22.paymentservice.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import it.polito.wa2.group22.paymentservice.dtos.UserDetails
import it.polito.wa2.group22.paymentservice.utils.listStringToListRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtUtils(@Value("\${jwt.key}") private val key: String) {

    private val parser: JwtParser =
        Jwts.parserBuilder().setSigningKey(Base64.getEncoder().encodeToString(key.toByteArray())).build()

    fun validateJwt(authToken: String): Boolean {
        try {
            val body = this.parser.parseClaimsJws(authToken).body

            val userId = body.getValue("sub").toString()
            val roles = listStringToListRole(
                body.getValue("roles") as List<String>
            )

            if (userId.isBlank() || roles.isEmpty() || roles.any { r -> r.equals("ROLE_USER") || r.equals("ROLE_ADMIN") })
                return false
            return true

        } catch (e: Exception) {
            return false
        }
    }

    fun getDetailsJwt(authToken: String): UserDetails {
        val body = this.parser.parseClaimsJws(authToken).body

        val userId = body.getValue("sub").toString()
        val role = (body["roles"] as List<*>)[0].toString()
        return UserDetails(userId, role)
    }
}