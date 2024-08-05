/*
 * Copyright 2019 The Android Open Source Project
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

package com.strime.contactapp.contacts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.ui.contacts.ContactsUiState
import com.strime.contactapp.ui.contacts.ContactsViewModel
import com.strime.sharedtestcode.data.ContactFactory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.strime.contactapp.R
import com.strime.sharedtestcode.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle

/**
 * Unit tests for the implementation of [ContactsViewModel]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    private lateinit var contactRepository: ContactRepository
    private lateinit var viewModel: ContactsViewModel

    @Test
    fun `loadMoreContact updates the state correctly`() = runTest {
        // Given
        contactRepository = mockk<ContactRepository>()
        coEvery { contactRepository.loadMoreData() } returns Unit

        val contacts = listOf(ContactFactory.createContactModel(id = 1, firstName = "Jon"))
        every { contactRepository.getContactsStream() } returns  flow { emit(contacts) }
        viewModel = ContactsViewModel(contactRepository)

        // When
        viewModel.loadMoreContact()


        // Then
        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.isLoading).isFalse()
            assertThat(uiState.networkFailed).isFalse()
            assertThat(uiState.items).isEqualTo(contacts)
            assertThat(uiState.userMessage).isNull()
        }
    }

    @Test
    fun `loadMoreContact handles error correctly`() = runTest {
        // Given
        contactRepository = mockk<ContactRepository>()
        val contacts = listOf(ContactFactory.createContactModel(id = 1, firstName = "Jon"))
        coEvery { contactRepository.loadMoreData() } throws RuntimeException("Network error")
        every { contactRepository.getContactsStream() } returns  flow { emit(contacts) }
        viewModel = ContactsViewModel(contactRepository)


        // When
        viewModel.loadMoreContact()


        // Then
        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.isLoading).isFalse()
            assertThat(uiState.networkFailed).isTrue()
            assertThat(uiState.items).isEqualTo(contacts)
            assertThat(uiState.userMessage).isEqualTo(R.string.loading_contact_error)
        }
    }


    @Test
    fun `snackbarMessageShown sets userMessage to null`() = runTest {
        contactRepository = mockk<ContactRepository>()
        every { contactRepository.getContactsStream() } answers { flow { emit(emptyList()) } }

        val viewModel = ContactsViewModel(contactRepository)

        viewModel._userMessage.value = R.string.loading_contact_error

        viewModel.snackbarMessageShown()

        assertThat(viewModel.uiState.value.userMessage).isNull()
    }
}
