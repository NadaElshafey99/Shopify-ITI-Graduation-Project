package com.example.shopify.Utilities

import android.app.Application
import com.example.shopify.core.helpers.RetrofitHelper
import com.example.shopify.core.utils.SharedPreference
import com.example.shopify.data.managers.CartManager
import com.example.shopify.data.managers.WishlistManager
import com.example.shopify.data.remote.product.RemoteResource
import com.example.shopify.data.remote.authentication.AuthenticationClient
import com.example.shopify.data.remote.authentication.IAuthenticationClient
import com.example.shopify.data.repositories.authentication.AuthRepository
import com.example.shopify.data.repositories.authentication.IAuthRepository
import com.example.shopify.data.repositories.product.IProductRepository
import com.example.shopify.data.repositories.product.ProductRepository
import com.example.shopify.data.repositories.user.IUserDataRepository
import com.example.shopify.data.repositories.user.UserDataRepository
import com.example.shopify.data.repositories.user.remote.IUserDataRemoteSource
import com.example.shopify.data.repositories.user.remote.UserDataRemoteSource
import com.example.shopify.data.repositories.user.remote.retrofitclient.RetrofitClient
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
    private val authenticationClient: IAuthenticationClient by lazy {
        AuthenticationClient(
            RetrofitHelper.getAuthenticationService(BASE_URL),
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance()
        )
    }

    val authRepository: IAuthRepository by lazy {
        AuthRepository(
            authenticationClient,
            SharedPreference.customPreference(applicationContext, CUSTOMER_PREF_NAME)
        )
    }
//    var currentCustomer: CollectCurrentCustomerData? = null
    override fun onCreate() = runBlocking{
        super.onCreate()
    }

    val cartManager: CartManager by lazy {
        CartManager(RetrofitClient.draftOrderAPI)
    }

    val wishlistManager: WishlistManager by lazy {
        WishlistManager(RetrofitClient.draftOrderAPI)
    }

    private val userDataRemoteSource: IUserDataRemoteSource by lazy {
        UserDataRemoteSource(
            RetrofitClient.customerAddressAPI,
            RetrofitClient.draftOrderAPI
        )
    }

    val userDataRepository: IUserDataRepository by lazy {
        UserDataRepository(
            userDataRemoteSource
        )
    }
}