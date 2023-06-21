package com.example.shopify.presentation.screens.brands


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.shopify.R
import com.example.shopify.core.helpers.UiState
import com.example.shopify.core.navigation.Screens
import com.example.shopify.data.managers.cart.CartManager
import com.example.shopify.data.managers.wishlist.WishlistManager
import com.example.shopify.data.models.Image
import com.example.shopify.data.models.Product
import com.example.shopify.data.models.ProductSample
import com.example.shopify.data.models.Products
import com.example.shopify.data.models.Variant
import com.example.shopify.data.repositories.product.IProductRepository
import com.example.shopify.presentation.common.composables.CustomSearchbar
import com.example.shopify.presentation.common.composables.LottieAnimation
import com.example.shopify.presentation.screens.homescreen.CardDesign
import com.example.shopify.presentation.screens.homescreen.FavoriteButton
import com.example.shopify.presentation.screens.homescreen.ImageFromNetwork
import com.example.shopify.utilities.ShopifyApplication


@SuppressLint("SuspiciousIndentation")
@Composable
fun BrandsScreen(navController: NavHostController, id: Long) {
    val repository: IProductRepository =
        (LocalContext.current.applicationContext as ShopifyApplication).repository
    val wishlistManager: WishlistManager =
        (LocalContext.current.applicationContext as ShopifyApplication).wishlistManager
    val cartManager: CartManager =
        (LocalContext.current.applicationContext as ShopifyApplication).cartManager
    val viewModel: BrandsViewModel = viewModel(
        factory = BrandsViewModelFactory(
            repository, wishlistManager, cartManager
        )
    )

    val productsState: UiState by viewModel.brandList.collectAsState()
    var productsList:List<ProductSample> by remember {
        mutableStateOf(listOf())
    }



    var searchText by remember { mutableStateOf("") }
    val isSearching by remember {
        derivedStateOf {
            searchText.isNotEmpty()
        }
    }

    var filteredState by rememberSaveable {
        mutableStateOf(productsList)
    }
    val filteredList by remember {
        derivedStateOf {
            if (searchText.isEmpty()|| searchText == "") {
                 productsList

            }else {

               productsList.filter { it.title?.contains(searchText, ignoreCase = true) ?: false }

            }
        }
    }

     LaunchedEffect(Unit) {

         if (id != null) {
             viewModel.id = id
             viewModel.getSpecificBrandProducts(id)
         }

     }


    when (productsState) {
        is UiState.Loading -> {
            LottieAnimation(animation = R.raw.loading_animation)
        }

        is UiState.Success<*> -> {
            productsList =
                (productsState as UiState.Success<Products>).data.body()?.products!!

        }

        else -> {
            Log.i("homepage", (productsState as UiState.Error).error.toString())
        }
    }

        Column() {

            CustomSearchbar(
                searchText = searchText,
                onTextChange = { searchText = it },
                hintText = R.string.search_brands,
                isSearching = isSearching,
                onCloseSearch = { searchText = "" }
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (filteredList.isNotEmpty()) {
            ProductsCards(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier,
                isFavourite = false,
                onFavouriteClicked = {
//                        product ->
//                    isFavourite = !isFavourite
//                    if (isFavourite) {
//                        viewModel.addWishlistItem(product)
//
//                    }
//                    if (!isFavourite) {
//                        viewModel.removeWishlistItem(product)
//
//                    }

                },
                onAddToCard = { product ->
                    viewModel.addItemToCart(product.id, product.variants[0].id)
                },
                products = filteredList,
            )
        }


    }
}


@Composable
fun ProductsCards(
    navController: NavHostController,
    viewModel: BrandsViewModel,
    modifier: Modifier = Modifier,
    products: List<ProductSample>,
    isFavourite: Boolean,
    onFavouriteClicked: (item: ProductSample) -> Unit,
    onAddToCard: (item: ProductSample) -> Unit
) {
    LazyColumn(
        modifier = modifier,

        // content padding
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 16.dp,
            end = 12.dp,
            bottom = 16.dp
        )
    ) {
        items(products) { item ->
            var isFavourite by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(key1 = Unit ) {
                isFavourite = viewModel.isFavorite(item.id)
            }
            CardDesign(onCardClicked = {
                navController.navigate(route = "${Screens.Details.route}/${item.id}")
            }) {
                ProductItem(isFavourite = isFavourite, onFavouritesClicked = { product ->

                    isFavourite = !isFavourite
                    if (isFavourite) {
                        viewModel.addWishlistItem(product.id, product.variants[0].id)

                    }
                    if (!isFavourite) {
                        viewModel.removeWishlistItem(product.id)

                    }

                }, onAddToCard = { onAddToCard(item) }, item = item, navController = navController)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    onFavouritesClicked: (item: ProductSample) -> Unit,
    onAddToCard: (item: ProductSample) -> Unit,
    item: ProductSample
) {
    Card(onClick = {
        navController.navigate(route = "${Screens.Details.route}/${item.id}")
    }, modifier = Modifier.height(200.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            item.image?.src?.let {
                ImageFromNetwork(
                    image = it,
                    modifier = Modifier
                        //.fillMaxHeight()
                        // .clip(RoundedCornerShape(15.dp))
                        .height(200.dp)
                        .background(color = Color.White)
                    //.align(Alignment.CenterHorizontally)

                )
            }
            Column() {
                item.title.let {
                    if (it != null) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp),
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {


                    item.variants?.get(0)?.price.let {
                        Text(
                            text = it.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FavoriteButton(
                        isFavourite = isFavourite,
                        onClicked = { onFavouritesClicked(item) })
                }
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { onAddToCard(item) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,

                        )
                ) {
                    Text(
                        text = stringResource(R.string.add_to_cart),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }


            }

        }

    }


}

@Preview
@Composable
fun ProductCardPreview() {
//    ProductsCards(
//    isFavourite = true,
//    onFavouriteClicked = {},
//    onAddToCard = {},
//        products = list,
//    )
}

