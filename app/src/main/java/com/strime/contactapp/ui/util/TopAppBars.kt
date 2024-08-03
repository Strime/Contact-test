/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strime.contactapp.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.strime.contactapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsTopAppBar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailTopAppBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.contact_details))
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun AccountsTopAppBarPreview() {
    MaterialTheme {
        Surface {
            AccountsTopAppBar()
        }
    }
}


@Preview
@Composable
private fun AccountDetailTopAppBarPreview() {
    MaterialTheme {
        Surface {
            AccountDetailTopAppBar { }
        }
    }
}