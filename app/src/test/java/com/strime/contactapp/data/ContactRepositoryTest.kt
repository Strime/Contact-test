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

package com.strime.contactapp.data

import com.google.common.truth.Truth.assertThat
import com.strime.contactapp.data.local.FakeContactDao
import com.strime.contactapp.data.network.FakeNetworkDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.strime.sharedtestcode.data.ContactFactory
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before

/**
 * Unit tests for [ContactRepository].
 */

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class ContactRepositoryTest {

    private val contactModel1 = ContactFactory.createContactModel(id = 1, firstName = "Jon")
    private val contactModel2 = ContactFactory.createContactModel(id = 2, firstName = "Jack")
    private val contactModel3 = ContactFactory.createContactModel(id = 3, firstName = "Sally")


    private val newContactFirstName = "Peter"
    private val newContactModel = ContactFactory.createContactModel(id = 4, firstName = newContactFirstName)
    private val newContacts = listOf(newContactModel)

    private val networkTasks = listOf(contactModel1, contactModel2).map { it.toDto() }
    private val localTasks = listOf(contactModel3.toEntity())

    // Test dependencies
    private lateinit var networkDataSource: FakeNetworkDataSource
    private lateinit var localDataSource: FakeContactDao

    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    // Class under test
    private lateinit var repository: ContactRepository

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        networkDataSource = FakeNetworkDataSource(contacts = mutableMapOf(1 to networkTasks))
        localDataSource = FakeContactDao(localTasks)
        // Get a reference to the class under test
        repository = ContactRepositoryImpl(
            networkDataSource = networkDataSource,
            localDataSource = localDataSource,
            dispatcher = testDispatcher,
            scope = testScope
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAccounts_emptyRepositoryAndUninitializedCache() = testScope.runTest {
        networkDataSource.contacts?.clear()

        assertThat(repository.getContactsStream().first().size).isEqualTo(0)
    }

    @Test
    fun getAccounts_withData() = testScope.runTest {

        assertThat(repository.getContactsStream().first().size).isEqualTo(3)
    }
}