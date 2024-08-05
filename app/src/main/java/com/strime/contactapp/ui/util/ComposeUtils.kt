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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.strime.contactapp.R
import okhttp3.internal.notify

val primaryDarkColor: Color = Color(0xFF263238)

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param loading (state) when true, display a loading spinner over [content]
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param content (slot) the main content to show
 */
@Composable
fun LoadingContent(
    loading: Boolean,
    error: Boolean,
    empty: Boolean,
    modifier: Modifier,
    emptyContent: @Composable () -> Unit,
    loadingContent: @Composable () -> Unit = { CircularProgressIndicator() },
    errorContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (empty) {
        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    loadingContent.invoke()
                }
            }

            error -> errorContent?.invoke() ?: Text("Oups")

            else -> emptyContent()
        }
    } else {
        val progressIndicatorAlpha = when (loading) {
            true -> 1F
            else -> 0F
        }
        Column(modifier = modifier.fillMaxSize(),) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(progressIndicatorAlpha)
            )
            content()
        }
    }
}

