package com.willowtreeapps.signinwithapplebutton

import android.net.Uri
import java.util.*

class SignInWithAppleService(
    private val clientId: String,
    private val redirectUri: String,
    private val scope: String,
    val callback: Callback
) {

    interface Callback {
        fun onSignInSuccess(authorizationCode: String)
        fun onSignInFailure(error: Throwable)
    }

    data class AuthenticationAttempt(
        val authenticationUri: String,
        val redirectUri: String,
        val state: String
    )

    /*
    The authentication page URI we're creating is based off the URI constructed by Apple's JavaScript SDK,
    which is why certain fields (like the version, v) are included in the URI construction.

    We have to build this URI ourselves because Apple's behavior in JavaScript is to POST the response,
    while we need a GET so we can retrieve the authentication code and verify the state
    merely by intercepting the URL.

    See the Sign In With Apple Javascript SDK for comparison:
    https://developer.apple.com/documentation/signinwithapplejs/configuring_your_webpage_for_sign_in_with_apple
    */
    fun buildAuthenticationAttempt(): AuthenticationAttempt {
        val state = UUID.randomUUID().toString()
        val authenticationUri = Uri
            .parse("https://appleid.apple.com/auth/authorize")
            .buildUpon().apply {
                appendQueryParameter("response_type", "code")
                appendQueryParameter("v", "1.1.6")
                appendQueryParameter("client_id", clientId)
                appendQueryParameter("redirect_uri", redirectUri)
                appendQueryParameter("scope", scope)
                appendQueryParameter("state", state)
            }
            .build()
            .toString()

        return AuthenticationAttempt(authenticationUri, redirectUri, state)
    }

}
