package it.polito.wa2.group22.authservice.services

import it.polito.wa2.group22.authservice.dto.*
import it.polito.wa2.group22.authservice.dto.ActivationDTO
import it.polito.wa2.group22.authservice.entities.Activation
import it.polito.wa2.group22.authservice.entities.User
import it.polito.wa2.group22.authservice.exceptions.AuthServiceBadRequestException
import it.polito.wa2.group22.authservice.exceptions.AuthServiceNotFoundException
import it.polito.wa2.group22.authservice.exceptions.AuthServicePanicException
import it.polito.wa2.group22.authservice.interfaces.*
import it.polito.wa2.group22.authservice.repositories.ActivationRepository
import it.polito.wa2.group22.authservice.repositories.UserRepository
import it.polito.wa2.group22.authservice.utils.RegistrationResult
import it.polito.wa2.group22.authservice.utils.ValidationResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID
import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

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
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var activationRepo: ActivationRepository


    /*
        'userRegister' function tests
    */
    @Test
    fun validUserRegisterTest() {
        val user = User("validUserRegisterTest", "Secret!Password1", "validUserRegisterTest@email.it")
        val userRegistered = userService.userRegister(user.toDTO())
        Assertions.assertTrue(userRegistered is RegisterResponseValid)
        val activation = activationRepo.findById((userRegistered as RegisterResponseValid).provisional_id)
        Assertions.assertNotNull(activation)
        Assertions.assertEquals(
            RegisterResponseValid(activation.get().id!!, activation.get().user.email),
            userRegistered
        )
    }


    /*
        'newActivation' function tests
    */
    @Test
    fun newActivationSuccess() {
        var result: ActivationDTO? = null
        val user = User("pablo", "password", "group22wa2@gmail.com")
        userRepo.save(user)
        Assertions.assertDoesNotThrow {
            result = userService.newActivation(user)
        }
        Assertions.assertNotNull(result?.email)
        Assertions.assertNotNull(result?.provisional_id)
    }


    /*
        'isValidUser' function tests
    */
    @Test
    fun validIsValidUserTest() {
        val user = User("test1", "Secret!Password1", "test1@email.it")
        val res = userService.isValidUser(user)
        Assertions.assertEquals(RegistrationResult.VALID_USER, res)
    }

    @Test
    fun blankUsernameNotValidUserTest() {
        val user = User("", "Secret!Password1", "blankUsername@email.it")
        val res = userService.isValidUser(user)
        Assertions.assertEquals(RegistrationResult.BLANK_USERNAME, res)
    }

    @Test
    fun blankPasswordNotValidUserTest() {
        val user = User("test3", "", "test3@email.it")
        val res = userService.isValidUser(user)
        Assertions.assertEquals(RegistrationResult.BLANK_PASSWORD, res)
    }

    @Test
    fun blankEmailNotValidUserTest() {
        val user = User("blankEmail", "Secret!Password1", "")
        val res = userService.isValidUser(user)
        Assertions.assertEquals(RegistrationResult.BLANK_EMAIL, res)
    }

    @Test
    fun invalidPasswordNotValidUserTest() {
        val user = User("test1", "Secret", "invalidPassword@email.it")
        val res = userService.isValidUser(user)
        Assertions.assertEquals(RegistrationResult.INVALID_PASSWORD, res)
    }

    @Test
    fun userNameIsNotUniqueNotValidUserTest() {
        val userOriginal = User("UserNameIsNotUniqueTest", "Secret!Password1", "usernameisnotuniquetest@email.it")
        val userDuplicate = User("UserNameIsNotUniqueTest", "Secret!Password1", "usernameisnotuniquetest2@email.it")

        val res = userService.isValidUser(userOriginal)
        userRepo.save(userOriginal)
        val res2 = userService.isValidUser(userDuplicate)

        Assertions.assertEquals(RegistrationResult.VALID_USER, res)
        Assertions.assertEquals(RegistrationResult.USERNAME_IS_NOT_UNIQUE, res2)
    }

    @Test
    fun emailIsNotUniqueUserTest() {
        val userOriginal = User("emailIsNotUniqueUserTest1", "Secret!Password1", "emailisnotuinqueusertests@email.it")
        val userEmailDuplicated =
            User("emailIsNotUniqueUserTest", "Secret!Password1", "emailisnotuinqueusertests@email.it")

        val res = userService.isValidUser(userOriginal)
        userRepo.save(userOriginal)
        val res2 = userService.isValidUser(userEmailDuplicated)

        Assertions.assertEquals(RegistrationResult.VALID_USER, res)
        Assertions.assertEquals(RegistrationResult.EMAIL_IS_NOT_UNIQUE, res2)
    }

    @Test
    fun invalidEmailNotValidUserTest() {
        val user = User("invalidEmail", "Secret!Password1", "invalidemailgmail.com")
        val res = userService.isValidUser(user)

        Assertions.assertEquals(RegistrationResult.INVALID_EMAIL, res)
    }

    /*
        userValidate function tests
    */
    @Test
    fun validUserValidateTest() {
        // Create a new User
        val user = User("validUserValidateTest", "Secret!Password1", "validUserValidateTest@gmail.com")

        // Register user created
        val userRegistered = userService.userRegister(user.toDTO())

        // Check if RegisterResponse is of type Valid
        Assertions.assertTrue(userRegistered is RegisterResponseValid)

        // Get the activation from the DB
        val activation = activationRepo.findById((userRegistered as RegisterResponseValid).provisional_id)

        // Check the activation is not NULL
        Assertions.assertNotNull(activation)

        // Get User salt to generate a new hashed pwd
        //val salt = userRepo.findByUsername(user.username)?.salt!!
        //user.password = BCrypt.hashpw(user.password, salt)

        // Create a ValidationResponseValid
        val activationUser = activation.get().user
        val responseValidation =
            ValidationResponseValid(activationUser.id!!, activationUser.username, activationUser.email)

        // Try to validate the user activation
        val response = userService.userValidate(activation.get().toDTO())

        // Check validation result
        Assertions.assertEquals(responseValidation, response)
    }


    @Test
    fun activationCodeIsBlankUserValidateTest() {

        val resp = assertThrows<AuthServiceNotFoundException> {userService.userValidate(
            ActivationDTO(
                "activationCodeIsBlankUserValidateTest@email.it",
                "",
                UUID.randomUUID(),
                Date()
            )
        )}
        Assertions.assertEquals(ValidationResult.INVALID_REQUEST.toString(), resp.message)


    }

    @Test
    fun provisionalIdNotExistUserValidateTest() {
        // Create a new User
        val user = User("provisionalIdNEValidateTest", "Secret!Password1", "provisionalIdNEValidateTest@gmail.com")

        // Register user created
        val userRegistered = userService.userRegister(user.toDTO())

        // Check if RegisterResponse is of type Valid
        Assertions.assertTrue(userRegistered is RegisterResponseValid)

        // Get the activation from the DB
        val activation = activationRepo.findById((userRegistered as RegisterResponseValid).provisional_id)

        // Check the activation is not NULL
        Assertions.assertNotNull(activation)


        val response = assertThrows<AuthServiceNotFoundException> {
            userService.userValidate(
                ActivationDTO(
                    "provisionalIdNEValidateTest@gmail.com",
                    activation.get().activationCode,
                    UUID.randomUUID(),
                    Date()
                )
            )}


        // Check validation result
        Assertions.assertEquals(ValidationResult.NOT_VALID_ID.toString(), response.message)
    }

    @Test
    fun expiredDateUserValidateTest() {
        // Create a new User
        val user = User("expiredDateUserValidateTest", "Secret!Password1", "expiredDateUserValidateTest@email.it")

        // Save User on DB
        val userTmp = userRepo.save(user)

        // Create a new Activation
        val activation = Activation(userTmp, "12345", Date())

        // Update activation with fake expiration date
        activationRepo.save(activation)

        // Get Activation from DB
        val tmpActivation = activationRepo.findByActivationCode("12345")

        Assertions.assertNotNull(tmpActivation)

        val response = assertThrows<AuthServiceBadRequestException> {userService.userValidate(
            ActivationDTO(
                "expiredDateUserValidateTest@email.it",
                "12345",
                tmpActivation?.id!!,
                Date()
            )
        ) }


        Assertions.assertEquals(ValidationResult.EXPIRED_VALIDATION.toString(), response.message)

    }


    @Test
    fun multipleAttemptUserValidateTest() {
        // Create e new user
        val user =
            User("multipleAttemptUserValidateTest", "Secret!Password1", "multipleAttemptUserValidateTest@gmail.com")

        // Registered user created
        val userRegistered = userService.userRegister(user.toDTO())

        // Check if RegisterResponse is of type Valid
        Assertions.assertTrue(userRegistered is RegisterResponseValid)

        // Get a valid activation from DB just to use a valid id
        val activation = activationRepo.findById((userRegistered as RegisterResponseValid).provisional_id)
        Assertions.assertNotNull(activation)

        // Create activationDTO with wrong activationCode 4 times
        val requestActivation1 = ActivationDTO(activation.get().user.email, "1", activation.get().id!!, Date())
        val response1 = assertThrows<AuthServiceNotFoundException> {userService.userValidate(requestActivation1)}
        Assertions.assertEquals(ValidationResult.NOT_VALID_ACTIVATION_CODE.toString(), response1.message)


        val requestActivation2 = ActivationDTO(activation.get().user.email, "2", activation.get().id!!, Date())
        val response2 = assertThrows<AuthServiceNotFoundException> {userService.userValidate(requestActivation2)}
        Assertions.assertEquals(ValidationResult.NOT_VALID_ACTIVATION_CODE.toString(), response2.message)

        val requestActivation3 = ActivationDTO(activation.get().user.email, "3", activation.get().id!!, Date())
        val response3 = assertThrows<AuthServiceNotFoundException> {userService.userValidate(requestActivation3)}
        Assertions.assertEquals(ValidationResult.NOT_VALID_ACTIVATION_CODE.toString(), response3.message)


        val requestActivation4 = ActivationDTO(activation.get().user.email, "4", activation.get().id!!, Date())
        val response4 = assertThrows<AuthServiceNotFoundException> {userService.userValidate(requestActivation4)}
        Assertions.assertEquals(ValidationResult.NOT_VALID_ACTIVATION_CODE.toString(), response4.message)

        val requestActivation5 = ActivationDTO(activation.get().user.email, "5", activation.get().id!!, Date())
        val response5 = assertThrows<AuthServicePanicException> {userService.userValidate(requestActivation5)}
        Assertions.assertEquals(ValidationResult.LIMIT_ATTEMPT.toString(), response5.message)


    }

    @Test
    fun pruneInactiveSuccess() {
        // Create a new User and save it on DB
        val user = User("pablo", "password", "group22wa222@gmail.com")
        val userSaved = userRepo.save(user)

        // Create a new Activation and save it on DB with expDate set to now for testing convenience
        val actSaved = activationRepo.save(Activation(user, "PRUNE_INACTIVE", Date()))

        // Check on DB the existence
        assert(userRepo.existsById(userSaved.id!!))
        assert(activationRepo.existsById(actSaved.id!!))

        // Just a sleep
        Thread.sleep(1_000)

        // Prune
        userService.pruneInactive()

        // Check the Prune success
        assert(!userRepo.existsById(userSaved.id!!))
        assert(!activationRepo.existsById(actSaved.id!!))
    }
}
