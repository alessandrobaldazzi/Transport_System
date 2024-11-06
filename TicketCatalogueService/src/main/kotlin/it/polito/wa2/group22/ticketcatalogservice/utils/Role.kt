package it.polito.wa2.group22.ticketcatalogservice.utils

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role {
    CUSTOMER, ADMIN, SUPERADMIN, MACHINE
}

// TODO: UPPERCASE
// TODO: decidere che tipo di oggetto arriva, se una stringa da parsare a lista o un array di stringhe
fun listStringToListRole(strings: List<String>): MutableList<SimpleGrantedAuthority> {
    val list = mutableListOf<SimpleGrantedAuthority>()
strings.forEach { a -> if (a == "CUSTOMER" || a == "ADMIN" || a == "SUPERADMIN" || a == "MACHINE" ) list.add(stringToRole[a]!!) }
    return list
}

val stringToRole = mapOf(
    "CUSTOMER" to SimpleGrantedAuthority("ROLE_"+Role.CUSTOMER.toString()),
    "ADMIN" to SimpleGrantedAuthority(Role.ADMIN.toString()),
    "SUPERADMIN" to SimpleGrantedAuthority(Role.SUPERADMIN.toString()),
    "MACHINE" to SimpleGrantedAuthority(Role.MACHINE.toString())
)