package it.polito.wa2.group22.ticketcatalogservice.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.ticketcatalogservice.dtos.UserDetailsDTO
import it.polito.wa2.group22.ticketcatalogservice.utils.listStringToListRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTUtils(
    @Value("\${jwt.key}") private val key: String
) {

    private val JWT_EXP_TIME = 1000 * 60 * 60  //1H

    private val parser: JwtParser =
        Jwts.parserBuilder().setSigningKey(Base64.getEncoder().encodeToString(key.toByteArray())).build()

    fun validateJwt(authToken: String): Boolean {
        try {
            val jwtParsed = parser.parseClaimsJws(authToken).body

            val user = jwtParsed.getValue("sub").toString()
            val roles = listStringToListRole(
                jwtParsed.getValue("roles") as List<String>
            )

            if (user.isBlank() || roles.isEmpty() || roles.any {
                        r -> r.equals("CUSTOMER") ||
                        r.equals("ADMIN") ||
                        r.equals("SUPERADMIN") ||
                        r.equals("MACHINE")
            })
                return false
            return true
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }


    fun getDetailsJwt(authToken: String): UserDetailsDTO {
        val jwtParsed = parser.parseClaimsJws(authToken)

        val user = jwtParsed.body.getValue("sub").toString()
        val roles = listStringToListRole(
            jwtParsed.body.getValue("roles") as List<String>
        )
        return UserDetailsDTO(user, roles)
    }

    fun generateToken(username: String, roles: List<String>): String{
        var username = username
        var roles = roles.map { it }
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_EXP_TIME))
            .claim("roles",roles)
            .signWith(Keys.hmacShaKeyFor(key.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

}