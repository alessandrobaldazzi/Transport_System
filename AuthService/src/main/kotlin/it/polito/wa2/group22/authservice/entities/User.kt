package it.polito.wa2.group22.authservice.entities

import it.polito.wa2.group22.authservice.dto.UserDTO
import it.polito.wa2.group22.authservice.entities.Role
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    var username: String = "",
    var password: String = "",
    var email: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(
        name = "user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1
    )
    @Column(name = "id")
    var id: Long? = null

  //  var salt: String = ""
    var isActive: Boolean = false

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
    var activation: Activation? = null
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @OnDelete(action = OnDeleteAction.CASCADE)
    var roles: MutableSet<Role> = mutableSetOf<Role>()
}
