package it.polito.wa2.group22.travelerservice.repositories

import it.polito.wa2.group22.travelerservice.entities.UserProfile
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional


@Repository
interface UserDetailsRepository : CrudRepository<UserProfile, String>{
    fun findByUsername(username: String): UserProfile?

    @Transactional
    @Modifying
    @Query("UPDATE UserProfile u set u.address = ?1, u.dateOfBirth = ?2, u.name = ?3, u.telephone = ?4  where u.username = ?5")
    fun updateUserProfileByUsername(address: String?, date_of_birth: String?, name: String?, telephone_number: String?, username: String): Int
}