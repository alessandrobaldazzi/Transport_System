package it.polito.wa2.group22.authservice.entities

import java.time.LocalDateTime
import java.util.Date
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "activations")
class Activation(
    @OneToOne(optional = false)
    var user: User,
    var activationCode: String,
    @Temporal(TemporalType.TIMESTAMP)
    var expDate: Date = java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(6))
) {
    @Id
    @GeneratedValue
    var id: UUID? = null

    var attempt: Int = 5
}