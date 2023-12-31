package com.example.shopify.presentation.screens.cartscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shopify.R
import com.example.shopify.core.helpers.UserScreenUISState
import com.example.shopify.core.navigation.Screens
import com.example.shopify.data.models.ProductSample
import com.example.shopify.data.repositories.cart.CartRepository
import com.example.shopify.presentation.common.composables.CartItemCard
import com.example.shopify.presentation.common.composables.LottieAnimation
import com.example.shopify.presentation.common.composables.NoConnectionScreen
import com.example.shopify.presentation.common.composables.NoData
import com.example.shopify.presentation.common.composables.WarningDialog
import com.example.shopify.ui.theme.lightMainColor
import com.example.shopify.ui.theme.onMainColor
import com.example.shopify.utilities.ShopifyApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController
) {
    val viewModel: CartViewModel = viewModel(
        factory =
        CartViewModelFactory(
            cartRepository = CartRepository(
                cartManager =
                (LocalContext.current.applicationContext as ShopifyApplication).cartManager
            )

        )
    )

    var cartHasItems by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect {
            snackbarHostState.showSnackbar(context.getString(it))
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            if (cartHasItems)
                ExtendedFloatingActionButton(
                    containerColor = onMainColor,
                    modifier = Modifier, onClick = {
                        if (cartHasItems)
                            navController.navigate(Screens.Checkout.route)
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.checkout),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(start = 4.dp),
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = ""
                        )
                    }
                }
        },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = lightMainColor
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.cart),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            val state by viewModel.screenState.collectAsState()
            when (state) {
                is UserScreenUISState.Loading -> {
                    LottieAnimation(animation = R.raw.loading_animation)
                }

                is UserScreenUISState.Success<*> -> {
                    val cartItems =
                        (state as UserScreenUISState.Success<*>).data as List<ProductSample>
                    cartHasItems = cartItems.isNotEmpty()
                    if (cartHasItems)
                        CartScreenContent(
                            viewModel = viewModel,
                            cartItems = cartItems,
                            navController = navController
                        )
                    else
                        NoData(message = "Add Some Products!")
                }

                is UserScreenUISState.NotConnected -> {
                    NoConnectionScreen()
                }

                is UserScreenUISState.NoData -> {
                    NoData(message = "Add Some Products!")
                }

                else -> {}
            }

        }
    }
}

@Composable
fun CartScreenContent(
    viewModel: CartViewModel,
    cartItems: List<ProductSample>,
    navController: NavHostController
) {
    CartItems(
        viewModel = viewModel, cartItems = cartItems, navController = navController
    )
}

@Composable
fun CartItems(
    viewModel: CartViewModel,
    cartItems: List<ProductSample>,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }
    var itemToRemove by remember { mutableStateOf<ProductSample?>(null) }
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cartItems) {
            var itemCount by remember { mutableStateOf(viewModel.getCartItemCount(it)) }
            CartItemCard(product = it,
                initialCount = itemCount,
                maxCount = it.variants[0].availableAmount!!,
                increase = {
                    if (itemCount < it.variants[0].availableAmount!!) {
                        itemCount++
                    }
                    viewModel.increaseCartItemCount(it)
                }, decrease = {
                    if (itemCount > 1) {
                        itemCount--
                        viewModel.decreaseCartItemCount(it)
                    } else {
                        itemToRemove = it
                        showDialog = true
                    }
                }) {
                navController.navigate(Screens.Details.route + "/${it.id}", builder = {
                    launchSingleTop = true
                })
            }
        }
    }

    if (showDialog) {
        WarningDialog(dialogTitle = stringResource(id = R.string.remove_product),
            message = stringResource(id = R.string.cart_item_removal_warning),
            dismissButtonText = stringResource(id = R.string.cancel),
            confirmButtonText = stringResource(id = R.string.remove),
            onConfirm = { itemToRemove?.let { viewModel.removeCart(it.id) } }) {
            showDialog = false
        }
    }
}

@Preview
@Composable
fun CartPreview() {
    CartScreen(
        navController = rememberNavController()
    )
}


