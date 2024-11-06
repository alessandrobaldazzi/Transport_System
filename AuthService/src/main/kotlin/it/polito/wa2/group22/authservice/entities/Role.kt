package it.polito.wa2.group22.authservice.entities

import it.polito.wa2.group22.authservice.utils.RoleName
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "roles")
class Role(
    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST])
    @OnDelete(action = OnDeleteAction.CASCADE)
    var users: MutableSet<User>,
    @Column(unique = true)
    var role: RoleName,

    ) {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var roleId: Long? = null
}