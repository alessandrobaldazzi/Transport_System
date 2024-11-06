package it.polito.wa2.group22.travelerservice.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.travelerservice.dto.TicketsToGenerateDTO
import it.polito.wa2.group22.travelerservice.dto.UserProfileDTO
import it.polito.wa2.group22.travelerservice.dto.toDTO
import it.polito.wa2.group22.travelerservice.entities.TicketPurchased
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceBadRequestException
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceNotFoundException
import it.polito.wa2.group22.travelerservice.interfaces.*
import it.polito.wa2.group22.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.group22.travelerservice.utils.QRCodeGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
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
class UserServiceTests {

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

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userService: UserService

    @Value("\${jwt.keyTicket}")
    lateinit var keyTicket: String

    @Test
    fun getUserProfileValid() {
        val user = UserProfile(
            "getUserProfileValid",
            "TestAddress1",
            "via XXX",
            "3364459928",
            "01/01/1995"

        )

        val userSaved = userDetailsRepository.save(user)
        val userReturned = userService.getUserProfile(userSaved.username)

        val userServiceResponseValid = UserServiceGetProfileResponse(
            UserProfileDTO(user.username, user.name, user.address, user.telephone, user.dateOfBirth)
        )
        Assertions.assertEquals(userServiceResponseValid, userReturned)
    }

    @Test
    fun getUserProfileNotValidUsername() {
        Assertions.assertThrows(
            TravelerServiceNotFoundException::class.java,
            { userService.getUserProfile("userName") }
        )
    }

    @Test
    fun insertAndupdateUserProfileValid() {

        // CASE 1: Insert new user
        val user = UserProfile(
            "insertAndupdateUserProfileValid",
            "TestUser1",
        )
        var res = userService.updateUserProfile(user.toDTO(), user.username)
        Assertions.assertEquals(UserServiceUpdateProfileResponse(true), res)

        //CASE 2: Update user
        val userUpdated = UserProfileDTO(
            "insertAndupdateUserProfileValid",
            "address",
            "21/07/1997",
            "3798415981",
            "01/01/1995"
        )
        res = userService.updateUserProfile(userUpdated, userUpdated.username)
        Assertions.assertEquals(UserServiceUpdateProfileResponse(false), res)

        //CHeck user is saved in db
        var userSaved = userService.getUserProfile(user.username)
        val userUpdatedData = UserServiceGetProfileResponse(userUpdated)
        Assertions.assertEquals(userSaved, userUpdatedData)
    }

    @Test
    fun updateUserProfileNotValidDate() {
        val user = UserProfile(
            "updateUserProfileNotValidDate",
            "TestUser1",
            "address",
            "3567765456",
            "31/31/31"
        )

        Assertions.assertThrows(
            TravelerServiceBadRequestException::class.java,
            { userService.updateUserProfile(user.toDTO(), user.username) }
        )
    }

    @Test
    fun updateUserProfileNotValidTelephone() {
        val user = UserProfile(
            "updateUserProfileNotValidTelephone",
            "TestUser1",
            "address",
            "3765545l65",
            "01/01/1995"
        )

        Assertions.assertThrows(
            TravelerServiceBadRequestException::class.java,
            { userService.updateUserProfile(user.toDTO(), user.username) }
        )

    }

    @Test
    fun getUserTicketsValid() {

        val user = UserProfile(
            "getUserTicketsValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        val userSaved = userDetailsRepository.save(user)

        // Generate ticket
        val zones = "1,2,3"
        val token = generateTicketToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)), zones)
        val ticket = TicketPurchased(
            zid = zones,
            userProfile = user
        )
        ticketPurchasedRepository.save(ticket)

        val ticketsRes = userService.getUserTickets(user.username)
        val ticketExpetedRes = UserServiceGetTicketsResponse(
            listOf(ticket.toDTO())
        )
        Assertions.assertEquals(ticketsRes, ticketExpetedRes)
    }
    @Test
    fun generateTicketORD(){
        val user = UserProfile(
            "getUserUserNotFound",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        val userSaved = userDetailsRepository.save(user)

        val ticketToGenerate = TicketsToGenerateDTO(
                "ORDINARY",
                3,
                "1",
                1,
                "getUserUserNotFound",
                false
        )
        val tickets = userService.generateTickets(ticketToGenerate)

        Assertions.assertEquals(tickets?.size, 1)
        Assertions.assertEquals(tickets!![0].type, ticketToGenerate.type)

    }

    @Test
    fun generateTicket(){
        val user = UserProfile(
                "11",
                "TestUser1",
                "TestAddress1",
                "28/21/1995",
                "3364459928"
        )


        Assertions.assertThrows(
                TravelerServiceNotFoundException::class.java,
                { userService.getUserTickets(user.username) }
        )
    }

    @Test
    fun getQRCodeValid() {
        val user = UserProfile(
            "getQRCodeValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        val userSaved = userDetailsRepository.save(user)

        // Generate ticket
        val zones = "1,2,3"
        val token = generateTicketToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)), zones)
        var ticket = TicketPurchased(
            zid = zones,
            userProfile = user
        )
        ticket.jws = token
        ticket = ticketPurchasedRepository.save(ticket)

        val qrCode = userService.getQRCodeTicket(ticket.id!!)
        if (qrCode is UserServiceGetQRCodeResponse)
            Assertions.assertEquals(
                ticket.jws,
                QRCodeGenerator.decodeQR(Base64.getDecoder().decode( qrCode.ticket.qrcode))
            )
        else
            Assertions.assertTrue(false)
    }


    fun generateTicketToken(username: String, exp: Date, zones: String): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("zid", zones)
            .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()
    }
}