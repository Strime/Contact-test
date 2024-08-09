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

package com.strime.contactapp.ui.contactdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.strime.contactapp.R
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.ui.util.AccountDetailTopAppBar
import com.strime.contactapp.ui.util.LoadingContent
import kotlin.reflect.KFunction1

@Composable
fun ContactDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle the event when it's triggered
    LaunchedEffect(uiState.action) {
       val action =  when(val action = uiState.action) {
            is ContactDetailAction.SendSms -> "sms:${action.phoneNumber}"
            is ContactDetailAction.DoCall -> "tel:${action.phoneNumber}"
            is ContactDetailAction.SendEmail -> "mailto:${action.emailAddress}"
            is ContactDetailAction.OpenMaps -> "google.navigation:q=${action.address}"
            null -> null
        }
        action?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(action)
            }
            context.startActivity(intent)
            viewModel.onActionHandled() // Reset the event after handling
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AccountDetailTopAppBar(onBack = onBack)
        }
    ) { paddingValues ->

        ContactContent(
            loading = uiState.isLoading,
            empty = uiState.contactModel == null && !uiState.isLoading,
            contactModel = uiState.contactModel,
            onSendSmsClick = viewModel::onSendSmsClick,
            onCallNumberClick = viewModel::onCallNumberClick,
            onSendEmailClick = viewModel::onSendEmailClick,
            onShowMapClick = viewModel::onShowMapClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ContactContent(
    loading: Boolean,
    empty: Boolean,
    contactModel: ContactModel?,
    modifier: Modifier = Modifier,
    onSendSmsClick: (String) -> Unit,
    onCallNumberClick: (String) -> Unit,
    onSendEmailClick: (String) -> Unit,
    onShowMapClick: (String) -> Unit,
) {
    val screenPadding = Modifier.padding(
        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
        vertical = dimensionResource(id = R.dimen.vertical_margin),
    )
    val commonModifier = modifier
        .fillMaxWidth()
        .then(screenPadding)

    LoadingContent(
        loading = loading,
        error = false,
        empty = empty,
        modifier = modifier,
        emptyContent = {
            Text(
                text = stringResource(id = R.string.no_data),
                modifier = commonModifier
            )
        },
    ) {
        Column(commonModifier.verticalScroll(rememberScrollState())) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .then(screenPadding),
            ) {
                if (contactModel != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(contactModel.pictureUrl)
                                    .crossfade(true)
                                    .build(),
                                loading = { CircularProgressIndicator() },
                                contentDescription = stringResource(R.string.contact_image_content_description),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(120.dp)
                            )
                            Box(
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.White)
                            ) {
                                Text(
                                    contactModel.countryFlag,
                                    style = MaterialTheme.typography.headlineMedium,
                                )
                            }
                        }
                        Text(
                            text = contactModel.nameWithTitle, style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.vertical_margin))
                        )
                        ListTile(
                            title = contactModel.cell,
                            subtitle = stringResource(R.string.contact_cell),
                            leading = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = stringResource(id = R.string.phone_image_content_description)
                                )
                            },
                            trailing = {
                                IconButton(onClick = { onSendSmsClick(contactModel.cell) }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Message,
                                        contentDescription = stringResource(id = R.string.phone_image_content_description),
                                    )
                                }
                            },
                            onClick = { onCallNumberClick(contactModel.cell) }
                        )
                        ListTile(
                            title = contactModel.phone,
                            subtitle = stringResource(R.string.contact_phone),
                            leading = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = stringResource(id = R.string.phone_image_content_description),
                                    modifier = Modifier.alpha(0f)
                                )
                            },
                            onClick = { onCallNumberClick(contactModel.phone) }
                        )
                        HorizontalDivider()
                        ListTile(
                            title = contactModel.email,
                            subtitle = stringResource(R.string.contact_mail),
                            leading = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = stringResource(id = R.string.mail_image_content_description)
                                )
                            },
                            onClick = { onSendEmailClick(contactModel.email) }
                        )
                        HorizontalDivider()
                        ListTile(
                            title = contactModel.address,
                            subtitle = stringResource(R.string.contact_address),
                            leading = {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = stringResource(id = R.string.address_image_content_description)
                                )
                            },
                            onClick = {  onShowMapClick(contactModel.address) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListTile(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (leading != null) {
            Box(modifier = Modifier.padding(end = 16.dp)) {
                leading()
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall)
            }
        }

        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                trailing()
            }
        }
    }
}
