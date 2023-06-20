package com.example.shopify.presentation.common.composables

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.shopify.data.models.ItemWithName
import com.example.shopify.data.models.currency.Currency
import com.example.shopify.presentation.screens.settingsscreen.TAG

@Preview(showSystemUi = true)
@Composable
fun MenuPrev() {
    SingleSelectionDropdownMenu(title = "Choose Currency", items = Currency.list) {
        Log.i(TAG, "MenuPrev: $it")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SingleSelectionDropdownMenu(
    title: String,
    items: List<T>,
    onSelect: (String) -> Unit
) where T : ItemWithName {
    var selection by remember { mutableStateOf(title) }
    var expandedState by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedState,
            onExpandedChange = { expandedState = !expandedState },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selection,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedState
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
            ExposedDropdownMenu(
                expanded = expandedState,
                onDismissRequest = { expandedState = false }) {
                items.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it.getFullName(),
                                style = TextStyle(textAlign = TextAlign.Center)
                            )
                        },
                        onClick = {
                            selection = it.getShortName()
                            onSelect(it.getName())
                            expandedState = false
                        })
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SelectionDropdownMenu(
    title: String,
    items: List<T>,
    onSelect: (T) -> Unit
) where T : ItemWithName {
    var selection by remember { mutableStateOf(title) }
    var expandedState by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedState,
            onExpandedChange = { expandedState = !expandedState },
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = selection,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedState
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer)
            )
            ExposedDropdownMenu(
                expanded = expandedState,
                onDismissRequest = { expandedState = false }) {
                items.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it.getFullName(),
                                style = TextStyle(textAlign = TextAlign.Center)
                            )
                        },
                        onClick = {
                            selection = it.getShortName()
                            onSelect(it)
                            expandedState = false
                        })
                }
            }

        }
    }
}
