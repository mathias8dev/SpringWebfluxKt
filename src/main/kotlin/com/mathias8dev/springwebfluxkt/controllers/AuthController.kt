package com.mathias8dev.springwebfluxkt.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.mathias8dev.springwebfluxkt.utils.fromJson
import com.nimbusds.jose.shaded.gson.Gson
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate


@RestController()
@RequestMapping("/oauth")
class AuthController {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val restTemplate: RestTemplate = RestTemplate()

    @GetMapping("/test")
    fun test(): String {
        return "AuthController"
    }

    @GetMapping("/google-callback")
    fun googleCallback(
        @RequestParam("code") code: String,
        @RequestParam("scope") scope: String,
        @RequestParam("authuser") authUser: String,
        @RequestParam("prompt") prompt: String
    ): ResponseEntity<*> {
        logger.debug("code: $code, scope: $scope, authuser: $authUser, prompt: $prompt")
        val accessToken = getOauthAccessTokenGoogle(code)
        val response = Gson().fromJson(accessToken, Map::class.java)
        logger.debug("Access Token 2: $accessToken")
        logger.debug("Access Token 3: $response")
        val userInfo = getProfileDetailsGoogle(response.get("access_token")!!.toString())
        logger.debug("User Info: $userInfo")
        val headers = HttpHeaders()
        headers.set(HttpHeaders.LOCATION, "http://localhost:5173")
        return ResponseEntity<Any>(headers, HttpStatus.MOVED_PERMANENTLY)
    }

    private fun getOauthAccessTokenGoogle(code: String): String? {
        val clientId = ""
        val clientSecret = ""

        val restTemplate = RestTemplate()
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("code", code)
        params.add("redirect_uri", "http://localhost:8088/oauth/google-callback")
        params.add("client_id", clientId)
        params.add("client_secret", clientSecret)
        params.add("scope", "https://www.googleapis.com/auth/userinfo.profile")
        params.add("scope", "https://www.googleapis.com/auth/userinfo.email")
        params.add("scope", "openid")
        params.add("grant_type", "authorization_code")

        val requestEntity: HttpEntity<MultiValueMap<String, String>> = HttpEntity(params, httpHeaders)

        val url = "https://oauth2.googleapis.com/token"
        val response = restTemplate.postForObject(url, requestEntity, String::class.java)
        return response
    }

    private fun getProfileDetailsGoogle(accessToken: String): Any {
        logger.debug("The access token is: $accessToken")
        val restTemplate = RestTemplate()
        val httpHeaders = HttpHeaders()
        httpHeaders.setBearerAuth(accessToken)

        val requestEntity: HttpEntity<String> = HttpEntity(httpHeaders)

        val url = "https://www.googleapis.com/oauth2/v2/userinfo"
        val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String::class.java)
        logger.debug("User Info: ${response.body}")
        val jsonObject: UserInfo = response.body!!.fromJson()
        logger.debug("User Info: $jsonObject")
        return jsonObject
    }
}

data class UserInfo(
    val id: String?,
    val email: String?,
    @JsonProperty("verified_email")
    val verifiedEmail: Boolean?,
    val language: String?,
    val picture: String?,
    val name: String?,
    @JsonProperty("given_name")
    val givenName: String?,
    @JsonProperty("family_name")
    val familyName: String?
)