package com.example.shopify.data.repositories.product

import com.example.shopify.data.models.Brand
import com.example.shopify.data.models.Products
import com.example.shopify.data.models.SmartCollections
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface IProductRepository {
    suspend fun getBrands(): Flow<Response<SmartCollections>>
    suspend fun getRandomproducts(): Flow<Response<Products>>
    suspend fun getSpecificBrandProducts(id:Long): Flow<Response<Products>>
}