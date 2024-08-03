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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import coil.request.ImageRequest
import com.strime.contactapp.R
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.ui.theme.MyApplicationTheme
import com.strime.contactapp.ui.util.AccountsTopAppBar
import com.strime.contactapp.ui.util.LoadingContent
import java.util.UUID
import androidx.compose.foundation.layout.Column as Column1

@Composable
fun ContactsScreen(
    onContactClick: (ContactModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            AccountsTopAppBar()
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        val listState = rememberLazyListState()

        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
                .collect { lastVisibleItem ->
                    if (lastVisibleItem != null && lastVisibleItem.index == uiState.items.size - 1) {
                        /* TODO */
                    }
                }
        }
        ContactsContent(
            loading = uiState.isLoading,
            contactModels = uiState.items,
            onContactClick = onContactClick,
            modifier = Modifier.padding(paddingValues),
            listState = listState
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { message ->
            /*val snackbarText = stringResource(message)
            LaunchedEffect(scaffoldState, viewModel, message, snackbarText) {
                scaffoldState.snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }*/
        }
    }
}

@Composable
fun ContactsContent(
    loading: Boolean,
    contactModels: List<ContactModel>,
    onContactClick: (ContactModel) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState
) {

    LoadingContent(
        loading = loading,
        empty = contactModels.isEmpty() && !loading,
        emptyContent = { ContactsEmptyContent(modifier) },
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(contactModel.pictureUrl)
                .crossfade(true)
                .build(),
            //placeholder = painterResource(R.drawable.drawer_item_color),
            contentDescription = "stringResource(R.string.description)", //TODO: change it
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape)
        )
        Column1(Modifier.weight(1F)) {
            Text(
                text = contactModel.name,
                style = MaterialTheme.typography.bodyLarge,
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
        Image(
            painter = painterResource(id = R.drawable.logo_no_fill),
            contentDescription = stringResource(R.string.no_contacts_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = R.string.no_contacts_all))
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