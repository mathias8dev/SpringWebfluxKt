package com.mathias8dev.springwebfluxkt.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class CustomJwtGrantedAuthoritiesConverter : Converter<Jwt?, Collection<GrantedAuthority?>?> {

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        return getAuthorities(jwt).map { SimpleGrantedAuthority(it) }
    }


    private fun getAuthorities(jwt: Jwt): Collection<String> {
        val scopes = getClaimsAsCollection(jwt, "scope").map {
            if (it.startsWith("SCOPE_")) it.uppercase() else "SCOPE_${it.uppercase()}"
        }
        val roles = getClaimsAsCollection(jwt, "roles").map {
            if (it.startsWith("ROLE_")) it.uppercase() else "ROLE_${it.uppercase()}"
        }
        val authorities = getClaimsAsCollection(jwt, "authorities")
        return mutableSetOf<String>().apply {
            addAll(scopes)
            addAll(roles)
            addAll(authorities)
        }
    }


    private fun getClaimsAsCollection(jwt: Jwt, claimName: String): Collection<String> {
        val authorities = jwt.getClaim<Any?>(claimName)
        return if (authorities is String) {
            if (authorities.contains(",")) {
                authorities.split(",").filter { it.isNotBlank() }.map { it.trim() }.toSet()
            } else if (authorities.trim().contains(" ")) {
                authorities.split(" ").filter { it.isNotBlank() }.map { it.trim() }.toSet()
            } else emptyList()
        } else if (authorities is Collection<*>) {
            authorities.map { it.toString() }.toSet()
        } else emptyList()
    }

}
