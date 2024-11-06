package it.polito.wa2.group22.authservice.repositories

import it.polito.wa2.group22.authservice.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {

    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun existsByUsername(username: String): Boolean
}