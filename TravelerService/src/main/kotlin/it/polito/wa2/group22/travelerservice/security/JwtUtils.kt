package it.polito.wa2.group22.travelerservice.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import it.polito.wa2.group22.travelerservice.dto.UserDetailsDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.utils.listStringToListRole
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

//TODO: completare

@Component
class JwtUtils(@Value("\${jwt.key}") private val key: String) {

    private val parser: JwtParser =
        Jwts.parserBuilder().setSigningKey(Base64.encodeBase64String(key.toByteArray())).build()

    /*val allowedRoles = listOf(SimpleGrantedAuthority(Role.ADMIN.toString()),
                              SimpleGrantedAuthority(Role.CUSTOMER.toString()))*/


    fun validateJwt(authToken: String): Boolean {
        return try {
            val jwtParsed = parser.parseClaimsJws(authToken)
            true
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }

    fun getDetailsJwt(authToken: String): UserDetailsDTO? {
        try {
            if (!validateJwt(authToken)) {
                return null
            }
            var jwtParsed = parser.parseClaimsJws(authToken)
            val user = jwtParsed.body.getValue("sub").toString()
            val roles = listStringToListRole(
                jwtParsed.body.getValue("roles") as List<String>
            )
            return UserDetailsDTO(username = user, roles = roles)
        } catch (e: Exception) {
            return null
        }


    }
}
