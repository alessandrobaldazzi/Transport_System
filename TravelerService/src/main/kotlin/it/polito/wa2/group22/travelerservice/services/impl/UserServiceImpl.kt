package it.polito.wa2.group22.travelerservice.services.impl

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.travelerservice.dto.*
import it.polito.wa2.group22.travelerservice.entities.TicketPurchased
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceNotFoundException
import it.polito.wa2.group22.travelerservice.entities.UserProfile
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceBadRequestException
import it.polito.wa2.group22.travelerservice.exceptions.TravelerServiceServicePanicException
import it.polito.wa2.group22.travelerservice.interfaces.*
import it.polito.wa2.group22.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.group22.travelerservice.services.UserService
import it.polito.wa2.group22.travelerservice.utils.QRCodeGenerator
import it.polito.wa2.group22.travelerservice.utils.TicketType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters.*
import java.util.*

@Service
class UserServiceImpl: UserService{


    @Autowired
    lateinit var userRepo: UserDetailsRepository

    @Autowired
    lateinit var ticketRepo: TicketPurchasedRepository

    @Value("\${jwt.key}")
    lateinit var key: String

    @Value("\${jwt.keyTicket}")
    lateinit var keyTicket: String

    private val telephoneRegularExp = Regex("^[0-9]{10}\$")
    private val dateRegularExp = Regex("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}\$")


    override fun getUserProfile(userName: String): UserServiceGetProfileResponse {
        val userProfileData = userRepo.findByUsername(userName)
        return if (userProfileData == null) {
            throw TravelerServiceNotFoundException("User not found")
        } else {
            UserServiceGetProfileResponse(
                UserProfileDTO(
                    userProfileData.username,
                    userProfileData.name,
                    userProfileData.address,
                    userProfileData.telephone,
                    userProfileData.dateOfBirth
                )
            )
        }
    }

    override fun updateUserProfile(userProfileDTO: UserProfileDTO, username: String): UserServiceUpdateProfileResponse {
        if (userProfileDTO.dateOfBirth != null && !validateDate(userProfileDTO.dateOfBirth))
            throw TravelerServiceBadRequestException("Date validation failed")

        if (userProfileDTO.telephone != null && !validateTelephone(userProfileDTO.telephone))
            throw TravelerServiceBadRequestException("Telephone validation failed")

        val user = userRepo.findByUsername(username)

        // insert new user
        if (user == null){
            userRepo.save(
                UserProfile(
                    username,
                    userProfileDTO.name,
                    userProfileDTO.address,
                    userProfileDTO.telephone,
                    userProfileDTO.dateOfBirth
                )
            )
            return UserServiceUpdateProfileResponse(result = true)
            // update user info
        }else {
            userRepo.updateUserProfileByUsername(
                userProfileDTO.address,
                userProfileDTO.dateOfBirth.toString(),
                userProfileDTO.name,
                userProfileDTO.telephone,
                username
            )
            return UserServiceUpdateProfileResponse(result = false)
        }
    }

    override fun getUserTickets(username: String): UserServiceGetTicketsResponse {
        val userDetails = userRepo.findByUsername(username)
        return if (userDetails == null )
            throw TravelerServiceNotFoundException("User not found")
        else {
            val tickets = ticketRepo.getAllTicketsByUserProfile(userDetails)
                .map { ticket -> ticket.toDTO() }
            return UserServiceGetTicketsResponse(tickets = tickets)
        }
    }

    override fun getQRCodeTicket(ticketId: Long): UserServiceGetQRCodeResponse {
        val ticket = ticketRepo.getTicketPurchasedById(ticketId)
        if (ticket == null)
            throw TravelerServiceNotFoundException("Ticket not found")
        try {
            val qrCode = QRCodeGenerator.generate(ticket.jws,250, 250)
            return UserServiceGetQRCodeResponse(QRCodeDTO(qrCode))
        }catch (exc: Exception){
            throw TravelerServiceServicePanicException("Error qrCode generation")
        }
    }

    // Generates the tickets requested
    override fun generateTickets(ticketsToGenerate: TicketsToGenerateDTO): List<TicketPurchasedDTO>? {
        // Gets user from the db
        val user = userRepo.findByUsername(ticketsToGenerate.username)
        if (user == null)
            throw TravelerServiceNotFoundException("User not found")

        val calendar = Calendar.getInstance()
        val purchasedTickets = mutableListOf<TicketPurchasedDTO>()

        for (i in 1..ticketsToGenerate.quantity) {

            // Creates the ticket
            val t = TicketPurchased(ticketsToGenerate.zones, user)
            t.type = ticketsToGenerate.type
            t.iat = java.sql.Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))

            when (TicketType.values().find { it.name == ticketsToGenerate.type }!!) {
                TicketType.ORDINARY -> {
                    val now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = java.sql.Timestamp.valueOf(now)
                    t.exp = java.sql.Timestamp.valueOf(now.plusHours(ticketsToGenerate.duration!!.toLong()))
                }
                TicketType.WEEKEND -> {
                    val saturday = LocalDateTime.now().with(DayOfWeek.SATURDAY).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(saturday))
                    t.exp = resetTime(calendar, java.sql.Timestamp.valueOf(saturday.plusHours(24 * 2)))
                }
                TicketType.WEEKLY -> {
                    val monday = LocalDateTime.now().with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(monday))
                    t.exp = resetTime(calendar, java.sql.Timestamp.valueOf(monday.plusHours(24 * 7)))
                }
                TicketType.MONTLY -> {
                    val firstDay = LocalDateTime.now().with(firstDayOfMonth()).truncatedTo(ChronoUnit.SECONDS)
                    val lastDay = LocalDateTime.now().with(lastDayOfMonth()).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(firstDay))
                    t.exp = resetTime(calendar, java.sql.Timestamp.valueOf(lastDay.plusHours(24)))
                }
                TicketType.YEARLY -> {
                    val firstDay = LocalDateTime.now().with(firstDayOfYear()).truncatedTo(ChronoUnit.SECONDS)
                    val lastDay = LocalDateTime.now().with(lastDayOfYear()).truncatedTo(ChronoUnit.SECONDS)
                    t.validFrom = resetTime(calendar, java.sql.Timestamp.valueOf(firstDay))
                    t.exp = resetTime(calendar, java.sql.Timestamp.valueOf(lastDay.plusHours(24)))
                }
            }

            // Saves the tickets
            val newTicket = ticketRepo.save(t)

            // Generates JWS
            val claims = mapOf<String, Any>(
                "sub" to newTicket.id.toString(),
                "iat" to newTicket.iat,
                "nbf" to newTicket.validFrom,
                "exp" to newTicket.exp,
                "zid" to newTicket.zid,
                "type" to newTicket.type!!,
            )
            val jws = Jwts.builder()
                .setClaims(claims)
                .signWith(Keys.hmacShaKeyFor(key.toByteArray()), SignatureAlgorithm.HS256)
                .compact()
            newTicket.jws = jws
            purchasedTickets.add(newTicket.toDTO())
            ticketRepo.save(newTicket)
        }

        return purchasedTickets
    }

    // Resets the time part of a Date object to zero
    private fun resetTime(calendar: Calendar, date: Date): Date {
        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }


    private fun validateField(date_of_birth: String, telephone_number: String): Boolean {
        return (dateRegularExp.matches(date_of_birth) || date_of_birth == null) &&
                (telephoneRegularExp.matches(telephone_number) || telephone_number == null)
    }

    private fun validateDate(date_of_birth: String): Boolean {
        return dateRegularExp.matches(date_of_birth)
    }

    private fun validateTelephone(telephone_number: String): Boolean {
        return telephoneRegularExp.matches(telephone_number)
    }

}