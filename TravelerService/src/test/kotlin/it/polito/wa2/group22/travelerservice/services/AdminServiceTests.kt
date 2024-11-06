package it.polito.wa2.group22.travelerservice.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.travelerservice.dto.toDTO
import it.polito.wa2.group22.travelerservice.entities.TicketPurchased
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetProfileResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetTravelerTicketResponse
import it.polito.wa2.group22.travelerservice.interfaces.AdminServiceGetTravelersResponse
import it.polito.wa2.group22.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import org.junit.jupiter.api.AfterEach
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


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminServiceTests {

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
    lateinit var userRepo: UserDetailsRepository

    @Autowired
    lateinit var ticketRepo: TicketPurchasedRepository

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var userService: UserService

    @Value("\${jwt.keyTicket}")
    lateinit var keyTicket: String

    @AfterEach
    fun deleteDB(){
        ticketRepo.deleteAll()
        userRepo.deleteAll()
    }


    @Test
    fun getTravelersValid() {
        val user1 = UserProfile(
            "getTravelersValid1",
            "TestUser1",
            "TestAddress1",
            "3801045554",
            "01/01/1995",
        )

        val user2 = UserProfile(
            "getTravelersValid2",
            "TestUser2",
            "TestAddress2",
            "4554532123",
            "01/02/1972"
        )

        userRepo.save(user1)
        userRepo.save(user2)

        val users = adminService.getTravelers()
        val expResponse = AdminServiceGetTravelersResponse(
            listOf(user1.toDTO(), user2.toDTO())
        )
        Assertions.assertEquals(users, expResponse)
    }

    @Test
    fun getTravelerTicketsValid() {
        val user = UserProfile(
            "getTravelerTicketsValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        userRepo.save(user)

        // Generate ticket
        val ticket1 = generateTicket(user)
        val ticket2 = generateTicket(user)
        ticketRepo.save(ticket1)
        ticketRepo.save(ticket2)


        val ticketsRes = adminService.getTravelerTickets(user.username)
        val ticketExpetedRes = AdminServiceGetTravelerTicketResponse(
            listOf(ticket1.toDTO(), ticket2.toDTO())
        )
        Assertions.assertEquals(ticketsRes, ticketExpetedRes)
    }

    @Test
    fun getTravelerProfileValid() {
        val user = UserProfile(
            "getTravelerProfileValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        userRepo.save(user)
        val res = adminService.getTravelerProfile(user.username)
        val expRes = AdminServiceGetProfileResponse(
            user.toDTO()
        )
        Assertions.assertEquals(res, expRes)
    }

    fun generateTicket(user: UserProfile): TicketPurchased{
        val zones = "1,2,3"
        val token = generateTicketToken(user.username, java.util.Date.from(Instant.now().plus(1, ChronoUnit.HOURS)), zones)
        val ticket = TicketPurchased(
            zid = zones,
            userProfile = user
        )
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