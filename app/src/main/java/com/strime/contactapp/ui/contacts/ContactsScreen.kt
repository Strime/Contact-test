/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strime.contactapp.ui.contacts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.strime.contactapp.R
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.ui.theme.MyApplicationTheme
import com.strime.contactapp.ui.util.AccountsTopAppBar
import com.strime.contactapp.ui.util.LoadingContent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.UUID
import androidx.compose.foundation.layout.Column as Column1

@Composable
fun ContactsScreen(
    onContactClick: (ContactModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            AccountsTopAppBar()
        },
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        var didReachBottom by remember { mutableStateOf(false) } // Used to avoid infinite call when bottom reach with no network

        val listState = rememberLazyListState()

        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                .map { it?.index }
                .distinctUntilChanged()
                .collect { lastVisibleItemIndex ->
                    if (lastVisibleItemIndex == uiState.items.size - 1 &&
                        !uiState.isLoading &&
                        !didReachBottom
                    ) {
                        didReachBottom = true
                        viewModel.loadMoreContact()
                    } else {
                        didReachBottom = false
                    }
                }
        }
        ContactsContent(
            loading = uiState.isLoading,
            networkFailed = uiState.networkFailed,
            contactModels = uiState.items,
            onContactClick = onContactClick,
            modifier = Modifier.padding(paddingValues),
            onRefreshClick = { viewModel.loadMoreContact() },
            listState = listState
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarHostState, viewModel, message, snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }
    }
}

@Composable
fun ContactsContent(
    loading: Boolean,
    networkFailed: Boolean,
    contactModels: List<ContactModel>,
    onRefreshClick: () -> Unit,
    onContactClick: (ContactModel) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {

    LoadingContent(
        loading = loading,
        error = networkFailed,
        empty = contactModels.isEmpty(),
        modifier = modifier,
        emptyContent = { ContactsEmptyContent(modifier) },
        errorContent = { ContactsFailedContent(modifier, onRefreshClick) },
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(state = listState) {
                items(contactModels) { contact ->
                    ContactItem(
                        contactModel = contact,
                        onTaskClick = onContactClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactItem(
    contactModel: ContactModel,
    onTaskClick: (ContactModel) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
            .clickable { onTaskClick(contactModel) }
            .padding(
                vertical = dimensionResource(id = R.dimen.list_item_padding),
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
            )
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(contactModel.pictureUrl)
                .crossfade(true)
                .build(),
            loading = { CircularProgressIndicator() },
            contentDescription = stringResource(R.string.contact_image_content_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape)
        )
        Column(Modifier.weight(1F)) {
            Text(
                text = contactModel.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.horizontal_margin)
                )
            )
            Text(
                text = contactModel.phone,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.horizontal_margin)
                )
            )
        }
        Text(
            text = contactModel.countryFlag,
            Modifier.padding(start = dimensionResource(id = R.dimen.horizontal_margin))
        )
    }
}

@Composable
private fun ContactsEmptyContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.ContactPhone,
            modifier = Modifier.size(64.dp),
            contentDescription = stringResource(id = R.string.error_image_content_description)
        )
        Text(stringResource(id = R.string.no_contacts_all))
    }
}

@Composable
private fun ContactsFailedContent(
    modifier: Modifier = Modifier,
    onRefreshClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.WifiOff,
            modifier = Modifier.size(64.dp),
            contentDescription = stringResource(id = R.string.error_image_content_description)
        )
        Text(
            stringResource(id = R.string.network_failed),
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.vertical_margin))
        )
        Button(onClick = onRefreshClick) {
            Text(stringResource(id = R.string.retry))
        }
    }
}


@Preview
@Composable
private fun ContactItemPreview() {
    MyApplicationTheme {
        ContactItem(
            contactModel = ContactModel(
                id = 1L,
                gender = "male",
                title = "Mr",
                firstName = "Jon",
                lastName = "Doe",
                phone = "0123456789",
                cell = "0123456789",
                city = "Paris",
                country = "France",
                pictureUrl = "",
                nat = "FR",
                email = "email@test.com",
                uuid = UUID.randomUUID().toString()
            ),
            onTaskClick = { }
        )
    }
}

@Preview
@Composable
private fun ContactsEmptyContentPreview() {
    MyApplicationTheme {
        ContactsEmptyContent()
    }
}