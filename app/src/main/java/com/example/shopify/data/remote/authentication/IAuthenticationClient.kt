package com.example.shopify.data.remote.authentication

import com.example.shopify.core.helpers.AuthenticationResponseState
import com.example.shopify.core.helpers.KeyFirebase
import com.example.shopify.data.models.CustomerFirebase
import com.example.shopify.data.models.CustomerRequestBody
import com.example.shopify.data.models.CustomerResponseBody
import com.google.firebase.auth.AuthCredential


private const val BASE_URL = "https://mad43-alex-and-team2.myshopify.com/"

interface IAuthenticationClient {
    suspend fun getSingleCustomerFromShopify(customerID: Long): AuthenticationResponseState
    suspend fun registerUserToShopify(customer: CustomerRequestBody): AuthenticationResponseState

    suspend fun registerUserToFirebase(
        email: String,
        password: String,
        customerID: Long
    ): AuthenticationResponseState

    suspend fun loginUserFirebase(email: String, password: String): AuthenticationResponseState
    suspend fun googleSignIn(credential: AuthCredential): AuthenticationResponseState
    fun checkedLoggedIn(responseBody: CustomerResponseBody? = null): AuthenticationResponseState
    suspend fun retrieveCustomerIDs(): CustomerFirebase
    fun addCustomerIDs(customerID: Long)
    fun updateCustomerID(key: KeyFirebase, newValue: Long)


}