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

package com.strime.contactapp.ui.contact

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.strime.contactapp.ui.contactdetail.ContactContent
import com.strime.contactapp.ui.contactdetail.ContactDetailScreen
import com.strime.sharedtestcode.data.ContactFactory
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for [ContactDetailScreen].
 */
@RunWith(AndroidJUnit4::class)
class ContactScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {

    }

    @Test
    fun withModel_exists() {
        // Given
        composeTestRule.setContent {
            ContactContent(
                loading = false,
                empty = false,
                contactModel = ContactFactory.createContactModel(1, "Jon", phone = "1234567890"),
                modifier = Modifier,
                onSendSmsClick = viewModel::onSendSmsClick,
            )
        }
        // When
        composeTestRule.onNodeWithText("Mr. Jon Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("0123456789").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mobile").assertIsDisplayed()
        composeTestRule.onNodeWithText("1234567890").assertIsDisplayed()
        composeTestRule.onNodeWithText("Landline").assertIsDisplayed()
        composeTestRule.onNodeWithText("JonDoe@test.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paris, France").assertIsDisplayed()
        composeTestRule.onNodeWithText("Address").assertIsDisplayed()
    }
}

