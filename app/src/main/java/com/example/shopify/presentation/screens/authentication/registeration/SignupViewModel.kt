package com.example.shopify.presentation.screens.authentication.registeration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shopify.core.helpers.AuthenticationResponseState
import com.example.shopify.data.models.CustomerRequestBody
import com.example.shopify.data.repositories.authentication.IAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val authRepository: IAuthRepository) : ViewModel() {

    private val _email: MutableLiveData<String> = MutableLiveData("")
    val email: LiveData<String> = _email
    private val _password: MutableLiveData<String> = MutableLiveData("")
    val password: LiveData<String> = _password

    private var _authResponse: MutableStateFlow<AuthenticationResponseState> =
        MutableStateFlow(AuthenticationResponseState.Loading)
    val authResponse = _authResponse.asStateFlow()

    fun registerUserToShopify(requestBody: CustomerRequestBody) {
        Log.i("TAG", "registerUserToShopify: send body ${requestBody.customer}")
        CoroutineScope(Dispatchers.IO).launch {
            val response = authRepository.registerUserToShopify(requestBody)
            checkShopifyLoggedInState(
                response,
                requestBody.customer.email,
                requestBody.customer.password
            )
        }
    }

    private fun registerUserToFirebase(email: String, password: String, customerId: Long) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    authRepository.registerUserToFirebase(email, password, customerId)
                _authResponse.value = response
            }
        }
    }

    private fun checkShopifyLoggedInState(
        responseState: AuthenticationResponseState,
        email: String,
        password: String
    ) {
        when (responseState) {
            is AuthenticationResponseState.Loading -> {
                Log.i("TAG", "checkLoggedInState: IS LOADING")
            }

            is AuthenticationResponseState.Success -> {
                Log.i("TAG", "checkLoggedInState: SUCCESS")
                Log.i("TAG", "checkLoggedInState: ${responseState.responseBody}")
                responseState.responseBody?.customer?.let  {
                    registerUserToFirebase(
                        email, password,
                        it.id,
                    )
                }
            }
            else ->{
                Log.i("TAG", "checkShopifyLoggedInState: ERROR")
                _authResponse.value = responseState
            }
        }
    }
}