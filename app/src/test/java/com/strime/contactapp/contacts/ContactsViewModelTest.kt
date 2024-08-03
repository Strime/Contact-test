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

import com.google.common.truth.Truth.assertThat
import com.strime.sharedtestcode.data.FakeContactRepository
import com.strime.contactapp.ui.contacts.ContactsViewModel
import com.strime.sharedtestcode.MainCoroutineRule
import com.strime.sharedtestcode.data.ContactFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [ContactsViewModel]
 */
@ExperimentalCoroutinesApi
class ContactsViewModelTest {

    // Subject under test
    private lateinit var contactsViewModel: ContactsViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var contactRepository: FakeContactRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        contactRepository = FakeContactRepository()
        val contactModel1 = ContactFactory.createContactModel(id = 1, firstName = "Jon")
        val contactModel2 = ContactFactory.createContactModel(id = 2, firstName = "Jack")
        val contactModel3 = ContactFactory.createContactModel(id = 3, firstName = "Sally")
        contactRepository.addTasks(contactModel1, contactModel2, contactModel3)

        contactsViewModel = ContactsViewModel(contactRepository)
    }

    @Test
    fun loadAllTasksFromRepository_loadingTogglesAndDataLoaded() = runTest {
        // Set Main dispatcher to not run coroutines eagerly, for just this one test
        Dispatchers.setMain(StandardTestDispatcher())

        // Trigger loading of tasks
        //contactsViewModel.refresh()

        // Then progress indicator is shown
        assertThat(contactsViewModel.uiState.first().isLoading).isTrue()

        // Execute pending coroutines actions
        advanceUntilIdle()

        // Then progress indicator is hidden
        assertThat(contactsViewModel.uiState.first().isLoading).isFalse()

        // And data correctly loaded
        assertThat(contactsViewModel.uiState.first().items).hasSize(3)
    }

}
