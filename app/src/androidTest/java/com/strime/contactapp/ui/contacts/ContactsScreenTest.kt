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

package com.strime.contactapp.ui.contacts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.strime.contactapp.HiltTestActivity
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.data.local.database.ContactDao
import com.strime.sharedtestcode.data.ContactFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Integration test for the Contact List screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@HiltAndroidTest
class ContactsScreenTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var repository: ContactRepository

    @Inject
    lateinit var contactDao: ContactDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun displayAccount_whenRepositoryHasNoData() = runTest {
        // Given
        withContext(Dispatchers.IO) {
            contactDao.deleteAll()
        }

        // When
        setContent()

        // Then
        composeTestRule.onNodeWithText("Jon").assertDoesNotExist()
    }

    @Test
    fun displayAccount_whenRepositoryHasOneData() = runTest {
        // Given
        val contact = ContactFactory.createContactEntity("Jon")

        withContext(Dispatchers.IO) {
            contactDao.deleteAll()
            contactDao.upsertAll(listOf(contact))
        }

        // When
        setContent()

        // Then
        composeTestRule.onNodeWithText("Jon Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("0123456789").assertIsDisplayed()
    }

    @Test
    fun displayAccount_whenRepositoryHasMultipleData() = runTest {
        // Given
        val contact1 = ContactFactory.createContactEntity("Jon", phone = "0123456789")
        val contact2 = ContactFactory.createContactEntity("Jack", phone = "1234567890")
        val contact3 = ContactFactory.createContactEntity("Joe", phone = "2345678901")
        withContext(Dispatchers.IO) {
            contactDao.deleteAll()
            contactDao.upsertAll(listOf(contact1, contact2, contact3))
        }

        // When
        setContent()

        // Then
        composeTestRule.onNodeWithText("Jon Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("0123456789").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jack Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("1234567890").assertIsDisplayed()
        composeTestRule.onNodeWithText("Joe Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("2345678901").assertIsDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    ContactsScreen(
                        viewModel = ContactsViewModel(repository),
                        onContactClick = { },
                    )
                }
            }
        }
    }

}
