package it.polito.wa2.group22.travelerservice.controllers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.travelerservice.dto.TicketsToGenerateDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.entities.TicketPurchased
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.group22.travelerservice.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerTests(@Value("\${jwt.key}") private val key: String) {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }


    @LocalServerPort
    var port: Int = 0

    @Value("\${jwt.keyTicket}")
    lateinit var keyTicket: String

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepo: UserDetailsRepository

    @Autowired
    lateinit var ticketRepo: TicketPurchasedRepository

    @Autowired
    lateinit var userService: UserService

    fun generateToken(username: String, exp: Date, roles: List<String> = listOf("CUSTOMER")): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
    }

    @Test
    fun getUserProfileValid() {
        val user = userRepo.save(
            UserProfile(
                "getUserProfileValid",
                "getUserProfileValidName",
                "getUserProfileValidAddress",
                "3798415981",
                "21/07/1997",
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getUserProfileExpirationToken() {
        val user = userRepo.save(
            UserProfile(
                "getUserProfileExpirationToken",
                "getUserProfileValidName",
                "getUserProfileValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getUserProfileNotValidTokenSignature() {
        val user = userRepo.save(
            UserProfile(
                "getUserProfileNotValidTokenSignature",
                "getUserProfileValidName",
                "getUserProfileValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer ${token}xxx")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getUserProfileNotValidUsername() {

        val headers = HttpHeaders()
        val token = generateToken("userName", Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun putUserProfileValid() {
        var body = UserProfileDTO(
            "putUserProfileValid",
            "name",
            "address",
            "3798415981",
            "21/07/1997",
        )

        val headers = HttpHeaders()
        val token = generateToken(body.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        var requestEntity = HttpEntity(body,headers)
        val url = "http://localhost:$port/v1/profile/"
        var response = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)

        body = UserProfileDTO(
            "putUserProfileValid",
            "name surname",
            "address",
            "3798415981",
            "21/07/1997",
        )
        requestEntity = HttpEntity(body,headers)
        response = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, Any::class.java, Any::class.java
        )
        //Assertions.assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        Assertions.assertEquals(HttpStatus.CREATED, response.statusCode)

    }

    @Test
    fun putUserProfileFailed() {

        val body = UserProfileDTO(
            "putUserProfileFailed",
            "address",
            "21/07/1997",
            "3798415981",
            "0/2997"
        )

        val headers = HttpHeaders()
        val token = generateToken("userName", Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity(body,headers)
        val url = "http://localhost:$port/v1/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, Any::class.java, Any::class.java
        )
        //Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

    }

    //GetMyTickets

   @Test
    fun getMyTicketsValid(){
        val user = userRepo.save(
            UserProfile(
                "getMyTicketsValid",
                "getTicketsValidName",
                "getTicketsValidAddress",
                "21/07/1997",
                "3798415981"
            ))

       val ticket1 = generateTicket(user)
       val ticket2 = generateTicket(user)
       ticketRepo.save(ticket1)
       ticketRepo.save(ticket2)

       val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/tickets/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getMyTicketsInvalid(){
        val headers = HttpHeaders()
        val token = generateToken("user", Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer ${token}")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/tickets/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        //Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)

    }

    @Test
    fun getQRCodeTicketValid(){
        val user = userRepo.save(
            UserProfile(
                "getQRCodeTicketValid",
                "getTicketsValidName",
                "getTicketsValidAddress",
                "3798415981",
                "21/07/1997",
            ))

        var ticket = generateTicket(user)
        ticket = ticketRepo.save(ticket)

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/v1/ticket/${ticket.id}"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun postGenerateTicket() {
        val user = userRepo.save(
                UserProfile(
                        "postGenerateTicket",
                        "postGenerateTicket",
                        "getUserProfileValidAddress",
                        "21/07/1997",
                        "3798415981"
                ))
        val body = TicketsToGenerateDTO(
                "ORDINARY",
                3,
                "1",
                1,
                "postGenerateTicket",
                false
        )

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)), listOf("TICKETCATALOGUESERVICE"))
        headers.set("Authorization", "Bearer ${token}")
        val requestEntity = HttpEntity(body,headers)
        val url = "http://localhost:$port/v1/ticket/generate"
        val response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }



    fun generateTicket(user: UserProfile): TicketPurchased {
        val zones = "1,2,3"
        val token = generateTicketToken(user.username, java.util.Date.from(Instant.now().plus(1, ChronoUnit.HOURS)), zones)
        val ticket = TicketPurchased(
            zid = zones,
            userProfile = user
        )
        ticket.jws = token
        return ticket
    }
    fun generateTicketToken(username: String, exp: java.util.Date, zones: String): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(java.util.Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("zid", zones)
            .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()
    }
}