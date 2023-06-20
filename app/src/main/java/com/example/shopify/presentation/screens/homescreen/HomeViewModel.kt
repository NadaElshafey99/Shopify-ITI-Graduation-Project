package com.example.shopify.presentation.screens.homescreen

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shopify.core.helpers.UiState
import com.example.shopify.data.managers.cart.CartManager
import com.example.shopify.data.managers.wishlist.WishlistManager
import com.example.shopify.data.repositories.product.IProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: IProductRepository,
    private val wishlistManager: WishlistManager,
    private val cartManager: CartManager,
) : ViewModel() {

    private var _brandsList: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val brandList: StateFlow<UiState> = _brandsList

    private var _randomList: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val randomList: StateFlow<UiState> = _randomList

    private var _favProduct: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val favProduct: StateFlow<Boolean> = _favProduct

    init {
        getBrands()
        getRandomProducts()


    }

     fun getBrands() {
        Log.i("nada","home")
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getBrands()
            withContext(Dispatchers.Main) {
                response
                    .catch {
                        _brandsList.value = UiState.Error(it)

                    }
                    .collect {
                        Log.i("menna", "getbrands")
                        _brandsList.value = UiState.Success(it)
                        Log.i("TAG", "getBrands: =======================>")

                    }

            }
        }
    }

     fun getRandomProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getRandomProducts()
            withContext(Dispatchers.Main) {
                response
                    .catch {
                        _randomList.value = UiState.Error(it)
                    }
                    .collect {
                        _randomList.value = UiState.Success(it)

                    }

            }
        }
    }


    fun addWishlistItem(productId: Long, variantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            wishlistManager.addWishlistItem(productId, variantId)
        }
    }

    fun isFavorite(productId: Long/*, variantId: Long*/) {
        viewModelScope.launch(Dispatchers.IO) {
         _favProduct.value = wishlistManager.isFavorite(productId/*, variantId*/)
        }
    }

    fun removeWishlistItem(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            wishlistManager.removeWishlistItem(productId)
        }
    }

    fun addItemToCart(productId: Long, variantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            cartManager.addCartItem(productId, variantId)
        }

    }

}

class HomeViewModelFactory(
    private val repository: IProductRepository,
    private val wishlistManager: WishlistManager,
    private val cartManager: CartManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            HomeViewModel(
                repository,
                wishlistManager,
                cartManager,
            ) as T else throw IllegalArgumentException("View Model Class Not Found !!!")
    }
}