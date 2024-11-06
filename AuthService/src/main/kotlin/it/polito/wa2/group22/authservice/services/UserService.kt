package it.polito.wa2.group22.authservice.services

import it.polito.wa2.group22.authservice.entities.Activation
import it.polito.wa2.group22.authservice.entities.User
import it.polito.wa2.group22.authservice.exceptions.EmailServiceException
import it.polito.wa2.group22.authservice.interfaces.*
import it.polito.wa2.group22.authservice.repositories.ActivationRepository
import it.polito.wa2.group22.authservice.repositories.UserRepository
import it.polito.wa2.group22.authservice.utils.RegistrationResult
import it.polito.wa2.group22.authservice.utils.ValidationResult
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.authservice.dto.*
import it.polito.wa2.group22.authservice.entities.Role
import it.polito.wa2.group22.authservice.exceptions.AuthServiceBadRequestException
import it.polito.wa2.group22.authservice.exceptions.AuthServiceNotFoundException
import it.polito.wa2.group22.authservice.exceptions.AuthServicePanicException
import it.polito.wa2.group22.authservice.kafka.Topics
import it.polito.wa2.group22.authservice.repositories.RoleRepository
import it.polito.wa2.group22.authservice.utils.EmailResult
import it.polito.wa2.group22.authservice.utils.RoleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder

import javax.annotation.PostConstruct

@Service
class UserService( @Autowired
                   @Qualifier("newRegistrationTemplate")
                   private val newRegistrationTemplate: KafkaTemplate<String, Any>): UserDetailsService{

    @Value("\${jwt.key}")
    lateinit var key: String

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository




    private val log = LoggerFactory.getLogger(javaClass)

   @Autowired
   lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder
    private val JWT_EXP_TIME = 1000 * 60 * 60  //1H
    private var codeLength = 15
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val passwordRegularExp =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private val emailRegularExp =
        Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")

    // new user registration
    fun userRegister(userDto: UserDTO): RegisterResponse {
        var userSaved= User(userDto.nickname, userDto.password, userDto.email)
        val user = User(userDto.nickname, userDto.password, userDto.email)
        var userRegistrationResult = isValidUser(user)
        if (userRegistrationResult != RegistrationResult.VALID_USER) {
            throw AuthServiceBadRequestException(userRegistrationResult.name)
        }

        user.password = bCryptPasswordEncoder.encode(user.password)
        bCryptPasswordEncoder.encode(user.password)



        try {
            userSaved = userRepository.save(user)
        } catch (e: Exception) {
            userRegistrationResult = RegistrationResult.DB_ERROR
            println("Error in database")
            e.printStackTrace()
        }

        val activationDTOResult: ActivationDTO? = newActivation(userSaved)

        return if(activationDTOResult == null){
            throw AuthServicePanicException(userRegistrationResult.name)
        } else{
            RegisterResponseValid(activationDTOResult.provisional_id, activationDTOResult.email!!)
        }


    }

    fun newActivation(user: User): ActivationDTO? {
        val activation: Activation?

        try {
            activation = activationRepository.save(Activation(user, generateCode()))
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }


            val result= emailService.sendMail(
                activation.user.email,
                activation.user.username,
                activation.activationCode,
                activation.expDate)

              if (result!=EmailResult.SUCCESS){
                   activationRepository.delete(activation)
                  throw AuthServicePanicException(result.toString())
                     }



        return activation.toDTO()
    }

    private fun generateCode(): String {
        return (1..codeLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }

    fun userValidate(request: ActivationDTO): ValidationResponse {
        //check field of the request
        if (request.activation_code.isNullOrBlank() or request.provisional_id.toString().isBlank())
            throw AuthServiceNotFoundException(ValidationResult.INVALID_REQUEST.toString())

        //take the activation from the DB
        val activation: Activation = activationRepository.findById(request.provisional_id).orElse(null)
            ?: throw AuthServiceNotFoundException(ValidationResult.NOT_VALID_ID.toString())

        //check activation code validity
        if (activation.activationCode != request.activation_code) {
            activation.attempt--
            if (activation.attempt == 0) {
                // delete user and relative activation
                userRepository.delete(activation.user)
                throw AuthServicePanicException(ValidationResult.LIMIT_ATTEMPT.toString())
            }
            try {
                activationRepository.save(activation)
            } catch (e: Exception) {
                println("Error in database")
                e.printStackTrace()
            }
            throw AuthServiceNotFoundException(ValidationResult.NOT_VALID_ACTIVATION_CODE.toString())
        }

        //Check expiration date
        if (activation.expDate.before(Date.from(Instant.now())))
            throw AuthServiceBadRequestException(ValidationResult.EXPIRED_VALIDATION.toString())

        //all the checks passed
        val modification = activation.user
        modification.isActive = true

        if (!roleRepository.existsByRole(RoleName.CUSTOMER)) {
            roleRepository.save(Role(mutableSetOf<User>(), RoleName.CUSTOMER))
        }
        var role = roleRepository.findByRole(RoleName.CUSTOMER)
        modification.roles.add(role!!)
        role.users.add(modification)



        try {
            userRepository.save(modification)
            roleRepository.save(role)
        } catch (e: Exception) {
            println("Error in database")
            e.printStackTrace()
        }
        activationRepository.delete(activation)

        //kafka
        val newRegistrationDTO: NewRegistrationDTO = NewRegistrationDTO(activation.user.username)
        log.info("Sending new registration to Kafka {}", newRegistrationDTO)
        val message: Message<NewRegistrationDTO> = MessageBuilder
            .withPayload(NewRegistrationDTO(
                activation.user.username
            ))
            .setHeader(KafkaHeaders.TOPIC, Topics.authToTraveler)
            .build()

        newRegistrationTemplate.send(message)
        log.info("Message sent with success")

        return ValidationResponseValid(activation.user.id!!, activation.user.username, activation.user.email)
    }

    fun isValidUser(user: User): RegistrationResult {
        return when {
            // username, password, and email address cannot be empty;
            user.username.isBlank() -> RegistrationResult.BLANK_USERNAME
            user.password.isBlank() -> RegistrationResult.BLANK_PASSWORD
            user.email.isBlank() -> RegistrationResult.BLANK_EMAIL
            // username and email address must be unique system-wide;
            userRepository.findByUsername(user.username) != null -> RegistrationResult.USERNAME_IS_NOT_UNIQUE
            userRepository.findByEmail(user.email) != null  -> RegistrationResult.EMAIL_IS_NOT_UNIQUE
            // validation password and email
            !user.email.matches(emailRegularExp) -> RegistrationResult.INVALID_EMAIL
            !user.password.matches(passwordRegularExp) -> RegistrationResult.INVALID_PASSWORD
            else -> RegistrationResult.VALID_USER
        }
    }

     fun login(username: String, password: String): String? {
        val user = userRepository.findByUsername(username)
        if (user != null && bCryptPasswordEncoder.matches(password, user.password)) {

             var username = user.username
            var roles = user.roles.map { it.role }.toList()
            return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + JWT_EXP_TIME))
                .claim("roles",roles)
                .signWith(Keys.hmacShaKeyFor(key.toByteArray()), SignatureAlgorithm.HS256)
                .compact()
        }
        throw AuthServiceBadRequestException("Credentials not valid")
    }




fun adminRegister(admin: UserAdminDTO): ValidationResponse? {
    var userSaved = User(admin.username, admin.password, admin.email)
    val userNew = User(admin.username, admin.password, admin.email)
    var userRegistrationResult = isValidUser(userNew)


    if (userRegistrationResult != RegistrationResult.VALID_USER) {
        throw AuthServiceBadRequestException(userRegistrationResult.toString())
    }
        val password = bCryptPasswordEncoder.encode(admin.password)

        var role = roleRepository.findByRole(admin.role)
        var user = User(admin.username,password,admin.email)
        user.isActive=true
        user = userRepository.save(user)
        user.roles.add(role!!)
        role.users.add(user)
        roleRepository.save(role)
        userRepository.save(user)

    try {
        user = userRepository.save(user)
        user.roles.add(role!!)
        role.users.add(user)
        roleRepository.save(role)
       userSaved= userRepository.save(user)
    } catch (e: Exception) {
        userRegistrationResult = RegistrationResult.DB_ERROR
        println("Error in database")
        e.printStackTrace()
    }


    return ValidationResponseValid(userSaved.id!!,userSaved.username,userSaved.email)
     }

    @PostConstruct
    fun createAdmin() {
        if (!roleRepository.existsByRole(RoleName.CUSTOMER)) {
            roleRepository.save(Role(mutableSetOf<User>(), RoleName.CUSTOMER))
        }
        if (!roleRepository.existsByRole(RoleName.ADMIN)) {
            roleRepository.save(Role(mutableSetOf<User>(), RoleName.ADMIN))
        }
        if (!roleRepository.existsByRole(RoleName.SUPERADMIN)) {
            roleRepository.save(Role(mutableSetOf<User>(), RoleName.SUPERADMIN))
        }
        if (!roleRepository.existsByRole(RoleName.MACHINE)) {
            roleRepository.save(Role(mutableSetOf<User>(), RoleName.MACHINE))
        }
        if (!userRepository.existsByUsername("admin")) {
            var admin = User("admin",  bCryptPasswordEncoder.encode("admin"),"admin@email.com")
            admin.isActive=true
            var roleA = roleRepository.findByRole(RoleName.ADMIN)
            admin = userRepository.save(admin)
            admin.roles.add(roleA!!)
            roleA.users.add(admin)
            roleA = roleRepository.save(roleA)
            admin = userRepository.save(admin)
        }
        if (!userRepository.existsByUsername("superadmin")) {
            var superadmin = User("superadmin",  bCryptPasswordEncoder.encode("superadmin"),"superadmin@email.com")
            superadmin.isActive=true
            var roleSA = roleRepository.findByRole(RoleName.SUPERADMIN)
            superadmin = userRepository.save(superadmin)
            superadmin.roles.add(roleSA!!)
            roleSA.users.add(superadmin)
            roleSA = roleRepository.save(roleSA)
            superadmin = userRepository.save(superadmin)
        }

        if (!userRepository.existsByUsername("customer")) {
            var customer = User("customer",  bCryptPasswordEncoder.encode("customer"),"customer@email.com")
            customer.isActive=true
            var roleC = roleRepository.findByRole(RoleName.CUSTOMER)
            customer = userRepository.save(customer)
            customer.roles.add(roleC!!)
            roleC.users.add(customer)
            roleC = roleRepository.save(roleC)
            customer = userRepository.save(customer)
        }

        //insert machine
        if (!userRepository.existsByUsername("machine")) {
            var machine = User("machine",bCryptPasswordEncoder.encode("machine"),"machine@email.com" )
            machine.isActive=true
            var roleA = roleRepository.findByRole(RoleName.MACHINE)
            machine = userRepository.save(machine)
            machine.roles.add(roleA!!)
            roleA.users.add(machine)
            roleA = roleRepository.save(roleA)
            machine = userRepository.save(machine)
        }
    }



    // Time Scheduled Prune: fixed at 1 hour
    @Scheduled(fixedDelay = 3600000)
    fun pruneInactive() {
        val filteredAct = activationRepository
            .getInactiveActivations()
            .filter { act -> act.expDate.before(Date()) }
            .map { act -> act.user.id }
        userRepository.deleteAllById(filteredAct)
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username!!)
            ?: throw UsernameNotFoundException("User not found in the database")
        println(user.username)
        return UserDetailsDTO(user)
    }
}