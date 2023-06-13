package com.example.shopify.Utilities

import android.app.Application
import com.example.shopify.core.helpers.AuthenticationResponseState
import com.example.shopify.core.helpers.RetrofitHelper
import com.example.shopify.core.utils.SharedPreference
import com.example.shopify.data.models.CollectCurrentCustomerData
import com.example.shopify.data.models.GetCurrentCustomer.getCurrentCustomer
import com.example.shopify.data.remote.product.RemoteResource
import com.example.shopify.data.remote.authentication.AuthenticationClient
import com.example.shopify.data.repositories.authentication.AuthRepository
import com.example.shopify.data.repositories.authentication.IAuthRepository
import com.example.shopify.data.repositories.product.IProductRepository
import com.example.shopify.data.repositories.product.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking

private const val BASE_URL = "https://mad43-alex-and-team2.myshopify.com/"
private const val CUSTOMER_PREF_NAME = "customer"

class ShopifyApplication : Application() {

    val repository: IProductRepository by lazy {
        ProductRepository(
            RemoteResource.getInstance(context = applicationContext)
        )
    }
    val authRepository: IAuthRepository by lazy {
        AuthRepository(
            AuthenticationClient(
                RetrofitHelper.getAuthenticationService(BASE_URL),
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance()
            ),
            SharedPreference.customPreference(applicationContext, CUSTOMER_PREF_NAME)
        )
    }
    var currentCustomer: CollectCurrentCustomerData? = null
    override fun onCreate() = runBlocking {
        super.onCreate()

        when (authRepository.checkedLoggedIn()) {
            is AuthenticationResponseState.Success -> { //Is loggedIn
                currentCustomer = getCurrentCustomer(authRepository)
            }
            else -> {  //IsNot LoggedIn
                currentCustomer = null
            }
        }

    }

}