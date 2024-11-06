package it.polito.wa2.group22.authservice.repositories

import it.polito.wa2.group22.authservice.entities.Role
import it.polito.wa2.group22.authservice.utils.RoleName
import org.springframework.data.repository.CrudRepository


interface RoleRepository : CrudRepository<Role, Long> {
    fun existsByRole(role: RoleName): Boolean
    fun findByRole(role: RoleName): Role?
}