package it.polito.wa2.group22.travelerservice.utils

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role {
    CUSTOMER,
    ADMIN,
    SUPERADMIN,
    MACHINE,
    TICKETCATALOGUESERVICE
}

fun listStringToListRole(strings: List<String>): MutableList<SimpleGrantedAuthority> {
    val list = mutableListOf<SimpleGrantedAuthority>()
    strings.forEach { a -> if (a == "CUSTOMER" || a == "ADMIN" || a == "TICKETCATALOGUESERVICE") list.add(stringToRole[a]!!) }
    return list
}

val stringToRole = mapOf(
    "CUSTOMER" to SimpleGrantedAuthority("ROLE_"+Role.CUSTOMER.toString()),
    "ADMIN" to SimpleGrantedAuthority("ROLE_"+Role.ADMIN.toString()),
        "TICKETCATALOGUESERVICE" to SimpleGrantedAuthority("ROLE_"+Role.TICKETCATALOGUESERVICE.toString())
)