package com.example.shopify.presentation.screens.product_details_screen


import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.shopify.R
import com.example.shopify.core.navigation.Screens
import com.example.shopify.data.models.SingleProduct
import com.example.shopify.presentation.common.composables.ShowCustomDialog
import com.example.shopify.presentation.common.composables.WarningDialog
import com.example.shopify.presentation.screens.onBoarding.PagerIndicator
import com.example.shopify.ui.theme.ibarraBold
import com.example.shopify.ui.theme.ibarraRegular
import com.example.shopify.ui.theme.mainColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailsContentScreen(
    modifier: Modifier = Modifier,
    productNavController: NavHostController,
    isFavorite: Boolean,
    showFavWarningDialog: Boolean,
    showReviewsDialog: Boolean,
    showToast: Boolean,
    showCartDialog: Boolean,
    userIsLoggedIn: Boolean,
    rating: Double,
    @StringRes buttonTitle: Int,
    @StringRes dialogMessage: Int,
    @StringRes toastMessage: Int,
    itemCount: Int,
    product: SingleProduct?,

    onFavoriteChanged: () -> Unit,
    onAcceptFavChanged: () -> Unit,
    onDismissFavChanged: () -> Unit,

    increaseItemCount: () -> Unit,
    decreaseItemCount: () -> Unit,

    addToCartAction: () -> Unit,
    onAcceptRemoveCart: () -> Unit,
    onDismissRemoveCart: () -> Unit,

    showReviews: () -> Unit,
    onDismissShowReview: () -> Unit,


    ) {
    Scaffold(containerColor = Color.Transparent, bottomBar =
    {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth(),
        )
        {
            AddToCartBottom(
                addToCartAction,
                increaseItemCount,
                decreaseItemCount,
                buttonTitle,
                itemCount,
            )
        }
    })
    { values ->
        LazyColumn(
            contentPadding = values,
            modifier = Modifier.padding(paddingValues = PaddingValues(20.dp))
        ) {
            items(1) {
                val pagerState = rememberPagerState()
                val colorMatrix = remember { ColorMatrix() }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.large,

                    )
                {
                    HorizontalPager(
                        pageCount = product?.images?.size ?: 0,
                        state = pagerState
                    ) { index ->
                        val pageOffset =
                            (pagerState.currentPage - index) + pagerState.currentPageOffsetFraction
                        val imageSize by animateFloatAsState(
                            targetValue = if (pageOffset != 0.0f) 0.75f else 1f,
                            animationSpec = tween(durationMillis = 300)
                        )
                        LaunchedEffect(key1 = imageSize) {
                            if (pageOffset != 0.0f) {
                                colorMatrix.setToSaturation(0f)
                            } else {
                                colorMatrix.setToSaturation(1f)
                            }
                        }
                        Box {
                            AsyncImage(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.7f)
                                    .aspectRatio(3f / 2f)
                                    .graphicsLayer {
                                        scaleX = imageSize
                                        scaleY = imageSize
                                    },
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(product?.images?.get(index)?.src)
                                    .placeholder(R.drawable.product_image_placeholder)
                                    .error(R.drawable.product_image_placeholder)
                                    .build(),
                                contentDescription = stringResource(id = R.string.product_image),
                                contentScale = ContentScale.FillBounds,
                                colorFilter = ColorFilter.colorMatrix(colorMatrix)
                            )

                            Surface(
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .size(32.dp)
                            ) {
                                IconButton(onClick = {
                                    Log.i("TAG", "back to previous screen")
                                    productNavController.popBackStack()
                                }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = stringResource(
                                            id = R.string.back
                                        )
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                            ) {
                                IconButton(onClick = onFavoriteChanged) {
                                    if (isFavorite) Icon(
                                        Icons.Default.Favorite,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = stringResource(
                                            id = R.string.is_fav
                                        )
                                    ) else Icon(
                                        painter = painterResource(id = R.drawable.is_not_fav),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = stringResource(
                                            id = R.string.is_not_fav
                                        )
                                    )
                                }
                            }

                        }

                    }
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        product?.images?.size?.let { it1 ->
                            PagerIndicator(
                                it1,
                                pagerState.currentPage,
                                modifier = Modifier.padding(top = 5.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                modifier = Modifier.weight(1.5f),
                                text = product?.title ?: "",
                                style = ibarraBold,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                minLines = 2,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .align(Alignment.CenterVertically),
                                text = "${product?.variants?.get(0)?.price} ${stringResource(id = R.string.Egp)}",
                                style = ibarraBold,
                                fontWeight = FontWeight.Bold,
                                color = mainColor,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RatingBar(
                                modifier = Modifier,
                                rating = rating,
                                stars = 5,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                onClick = showReviews,
                                color = Color.Transparent,
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(id = R.string.show_reviews),
                                    style = ibarraBold,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 16.sp,
                                )
                            }

                        }


                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(id = R.string.size),
                            style = ibarraBold,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SizeOptions(product?.variants ?: listOf())
                        Spacer(modifier = Modifier.height(8.dp))

                        Divider(
                            color = MaterialTheme.colorScheme.surfaceTint,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.color),
                            style = ibarraBold,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorOptions(product?.options ?: listOf())
                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(
                            color = MaterialTheme.colorScheme.surfaceTint,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.description),
                            style = ibarraBold,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product?.description ?: "",
                            style = ibarraRegular,
                            color = Color.Black,
                            fontSize = 18.sp
                        )

                    }
                }
            }
        }
        if (showCartDialog) {
            ShowCustomDialog(
                title = R.string.remove_product,
                description = R.string.cart_item_removal_warning,
                buttonText = R.string.remove,
                animatedId = R.raw.remove_from_cart,
                onClickButton = onAcceptRemoveCart,
                onClose = onDismissRemoveCart
            )
        }
        if (showFavWarningDialog) {
            ShowCustomDialog(
                title = if (isFavorite)  R.string.remove_product_from_fav else R.string.add_product_to_fav,
                description = dialogMessage,
                buttonText = if (isFavorite)R.string.remove else R.string.add ,
                animatedId =if (isFavorite) R.raw.remove_from_favorite else R.raw.added_to_favourite  ,
                onClickButton = onAcceptFavChanged,
                onClose = onDismissFavChanged
            )

        }
        if (showReviewsDialog)
            ShowReviews(showReviewsDialog, onDismissShowReview)

        if (showToast) {
            Toast.makeText(
                LocalContext.current,
                stringResource(id = toastMessage),
                Toast.LENGTH_LONG
            ).show()
        }
        if (!userIsLoggedIn) {
            Surface(color = Color.Gray) {
                ShowCustomDialog(
                    title = R.string.login,
                    description = R.string.please_login,
                    buttonText = R.string.login,
                    animatedId = R.raw.sign_for_error_or_explanation_alert,
                    onClickButton = { productNavController.navigate(Screens.Login.route) },
                    onClose = onDismissRemoveCart
                )
            }
        }

    }
}

@Composable
fun AddToCartBottom(
    addToCartAction: () -> Unit,
    increase: () -> Unit,
    decrease: () -> Unit,
    @StringRes buttonTitle: Int,
    itemCount: Int
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(if (buttonTitle == R.string.check_out) mainColor else Color.Gray)
                    .width(200.dp)
                    .clickable {
                        addToCartAction()
                        Log.i("TAG", "view model to cart: ")
                    }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { Log.i("TAG", "Add to cart: ") }
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            stringResource(id = R.string.add_to_cart),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = stringResource(id = buttonTitle),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )

                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                    .background(Color.White)
                    .width(125.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = increase
                    ) {
                        Icon(
                            Icons.Default.Add,
                            stringResource(id = R.string.add)
                        )
                    }
                    Text(
                        text = itemCount.toString(),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            textAlign = TextAlign.Center
                        )

                    )
                    Log.i("TAG", "ProductDetailsContentScreen: $itemCount")
                    IconButton(
                        onClick = decrease

                    ) {
                        Icon(
                            painterResource(id = R.drawable.remove),
                            stringResource(id = R.string.remove)
                        )

                    }

                }
            }
        }
    }
}