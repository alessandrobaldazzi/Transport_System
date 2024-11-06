package it.polito.wa2.group22.authservice.repositories

import it.polito.wa2.group22.authservice.entities.Activation
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ActivationRepository : CrudRepository<Activation, UUID> {
    fun findByActivationCode(activationCode: String): Activation?;

    @Query(
        "SELECT * FROM activations AS A, users AS U WHERE A.user_id = U.id AND U.is_active = false",
        nativeQuery = true
    )
    fun getInactiveActivations(): List<Activation>
}