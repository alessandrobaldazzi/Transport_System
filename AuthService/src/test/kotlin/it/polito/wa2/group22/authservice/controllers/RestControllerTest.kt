package it.polito.wa2.group22.authservice.controllers

import it.polito.wa2.group22.authservice.dto.ActivationDTO
import it.polito.wa2.group22.authservice.dto.UserDTO
import it.polito.wa2.group22.authservice.dto.toDTO
import it.polito.wa2.group22.authservice.entities.User
import it.polito.wa2.group22.authservice.repositories.ActivationRepository
import it.polito.wa2.group22.authservice.repositories.UserRepository
import it.polito.wa2.group22.authservice.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RestControllerTest {

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

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var userService: UserService





    @Test
    fun userRegisterValidTest(){
        val baseUrl = "http://localhost:$port"
        val userDto = UserDTO(null, "userRegisterValidTest","Secret!Password1","userRegisterValidTest@email.it")

        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
                "$baseUrl/user/register",
                request
        )

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)
    }

    @Test
    fun userRegisterNotValidEmail(){
        val baseUrl = "http://localhost:$port"
        val userDto = UserDTO(null, "userRegisterNotValidEmail","Secret!Password1","userRegisterNotValidEmail!email.it")
        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun userRegisterNotValidPassword() {
        val baseUrl = "http://localhost:$port"
        val userDto = UserDTO(null, "userRegisterNotValidPassword","SecretPassword1","userRegisterNotValidPassword@email.it")
        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun userRegisterBlankUsername() {
        val baseUrl = "http://localhost:$port"
        val userDto = UserDTO(null, "","SecretPassword1","userRegisterBlankUsername@email.it")
        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun userRegisterBlankEmail() {
        val baseUrl = "http://localhost:$port"
        val userDto = UserDTO(null, "userRegisterBlankEmail","SecretPassword1","")
        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun userRegisterBlankPassword() {
        val baseUrl = "http://localhost:$port"
        val userDto = UserDTO(null, "userRegisterBlankpassword","","userRegisterBlankpassword@email.it")
        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun userRegisterEmailNotUnique() {
        val baseUrl = "http://localhost:$port"

        val userDto = UserDTO(null, "userRegisterEmailNotUniquefirst","Secret!Password1","userRegisterEmailNotUniquefirst@email.it")
        val request = HttpEntity(userDto)

        val userDtoDuplicated = UserDTO(null, "userRegisterEmailNotUnique2","Secret!Password1","userRegisterEmailNotUniquefirst@email.it")
        val requestDuplicated = HttpEntity(userDtoDuplicated)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )

        val responseDuplicated = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            requestDuplicated
        )

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseDuplicated.statusCode)
    }

    @Test
    fun userRegisterUsernameNotUnique() {
        val baseUrl = "http://localhost:$port"

        val userDto = UserDTO(null, "userRegisterUsernameNotUniquefirst","Secret!Password1","userRegisterUsernameNotUniquefirst@email.it")
        val request = HttpEntity(userDto)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            request
        )
        val userDtoDuplicated = UserDTO(null, "userRegisterUsernameNotUniquefirst","Secret!Password1","userRegisterUsernameNotUnique2@email.it")
        val requestDuplicated = HttpEntity(userDtoDuplicated)

        val responseDuplicated = restTemplate.postForEntity<String>(
            "$baseUrl/user/register",
            requestDuplicated
        )

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.statusCode)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseDuplicated.statusCode)
    }

    @Test
    fun userValidateEmptyActivationTest() {
        val baseUrl = "http://localhost:$port"

        // Create a new User and register it
        userService.userRegister(UserDTO(null, "userValidateEmptyActivationTest", "Secret!Password1", "userValidateEmptyActivationTest@email.it"))

        // Get the registered User from DB
        val userRegistered = userRepository.findByEmail("userValidateEmptyActivationTest@email.it")

        // Check user is not null
        Assertions.assertNotNull(userRegistered)

        // Check user Activation is not null
        Assertions.assertNotNull(userRegistered!!.activation)

        // Get the Activation from DB
        val activation = activationRepository.findById(userRegistered.activation?.id!!).get()

        // Check Activation is not null
        Assertions.assertNotNull(activation)

        // Generate ActivationDTO and HttpEntity
        val actDTO = activation.toDTO()
        val request = HttpEntity(ActivationDTO(userRegistered.email, "", actDTO.provisional_id, actDTO.expDate))

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request,
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun userValidateMissingActivationTest() {
        val baseUrl = "http://localhost:$port"

        val user = User("userValidateMissingActivationTest", "Secret!Password1", "userValidateMissingActivationTest@gmail.com")
        // Create a new User and register it
        userRepository.save(user)

        // Get the registered User from DB
        val userRegistered = userRepository.findByEmail("userValidateMissingActivationTest@gmail.com")

        // Check user is not null
        Assertions.assertNotNull(userRegistered)

        // Generate ActivationDTO and HttpEntity
        val actDTO = ActivationDTO(user.email,"12345",UUID.randomUUID(), java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(6)))
        val request = HttpEntity(actDTO)

        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request,
        )

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun userValidationWrongActivationTest() {
        val baseUrl = "http://localhost:$port"

        // Create a new User and register it
        userService.userRegister(UserDTO(null, "userValidationWrongActivationTest", "Secret!Password1", "userValidationWrongActivationTest@email.it"))

        // Get the registered User from DB
        val userRegistered = userRepository.findByEmail("userValidationWrongActivationTest@email.it")

        // Check user is not null
        Assertions.assertNotNull(userRegistered)

        // Check user Activation is not null
        Assertions.assertNotNull(userRegistered!!.activation)

        // Get the Activation from DB
        val activation = activationRepository.findById(userRegistered.activation?.id!!).get()

        // Check Activation is not null
        Assertions.assertNotNull(activation)

        // Generate ActivationDTO and HttpEntity
        val actDTO = activation.toDTO()
        val request = HttpEntity(ActivationDTO(actDTO.email, "GAGAGA", actDTO.provisional_id, actDTO.expDate))

        // Try validation
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request
        )

        // Check result validation
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun userValidationWrongIdTest() {
        val baseUrl = "http://localhost:$port"

        // Create a new User and register it
        userService.userRegister(UserDTO(null, "userValidationWrongIdTest", "Secret!Password1", "userValidationWrongIdTest@email.it"))

        // Get the registered User from DB
        val userRegistered = userRepository.findByEmail("userValidationWrongIdTest@email.it")

        // Check user is not null
        Assertions.assertNotNull(userRegistered)

        // Check user Activation is not null
        Assertions.assertNotNull(userRegistered!!.activation)

        // Get the Activation from DB
        val activation = activationRepository.findById(userRegistered.activation?.id!!).get()

        // Check Activation is not null
        Assertions.assertNotNull(activation)

        // Generate ActivationDTO and HttpEntity
        val actDTO = activation.toDTO()
        val request = HttpEntity(ActivationDTO(actDTO.email, actDTO.activation_code, UUID.randomUUID(), actDTO.expDate))

        // Try validation
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request
        )

        // Check result validation
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun userValidateValidTest() {
        val baseUrl = "http://localhost:$port"

        // Create a new User and register it
        userService.userRegister(UserDTO(null, "userValidateValidTest", "Secret!Password1", "userValidateValidTest@email.it"))

        // Get the registered User from DB
        val userRegistered = userRepository.findByEmail("userValidateValidTest@email.it")

        // Check user is not null
        Assertions.assertNotNull(userRegistered)

        // Check user Activation is not null
        Assertions.assertNotNull(userRegistered!!.activation)

        // Get the Activation from DB
        val activation = activationRepository.findById(userRegistered.activation?.id!!).get()

        // Check Activation is not null
        Assertions.assertNotNull(activation)

        // Generate ActivationDTO and HttpEntity
        val actDTO = activation.toDTO()
        val request = HttpEntity(actDTO)

        // Try validation
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request
        )

        // Check result validation
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun userValidateInvalidParsingActivationTest() {
        val baseUrl = "http://localhost:$port"
        //val act = ActivationDTO("", "", UUID.randomUUID(), Date())
        // Sending a strange JSON Object
        val request = HttpEntity(UserDTO(null, "userValidateInvalidActivationTest", "Secret!Password1", "userValidateInvalidActivationTest@gmail.com"))
        val response = restTemplate.postForEntity<String>(
            "$baseUrl/user/validate",
            request,
            )

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
}