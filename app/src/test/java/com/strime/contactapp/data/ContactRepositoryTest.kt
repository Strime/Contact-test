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
import com.strime.contactapp.data.local.database.ContactDao
import com.strime.contactapp.data.network.NetworkDataSource
import com.strime.contactapp.data.network.NetworkDataSourceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.strime.sharedtestcode.data.ContactFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
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

    // Test dependencies
    private val networkDataSource: NetworkDataSource= mockk(relaxed = true)
    private val localDataSource: ContactDao= mockk(relaxed = true)

    private var testDispatcher = UnconfinedTestDispatcher()
    private var testScope = TestScope(testDispatcher)

    // Class under test
    private lateinit var repository: ContactRepository

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        // Get a reference to the class under test
        repository = ContactRepositoryImpl(
            networkDataSource = networkDataSource,
            localDataSource = localDataSource,
            dispatcher = testDispatcher,
            scope = testScope
        )
    }
    @Test
    fun `getContactsStream returns data from localDataSource`() = runTest {
        val contacts = listOf(
            ContactFactory.createContactEntity(firstName = "John")
        )
        every { localDataSource.observeAll() } returns flowOf(contacts)

        val result = repository.getContactsStream().first()

        assertThat(result).isEqualTo(contacts.toModel())
    }

    @Test
    fun `getContactStream returns data from localDataSource`() = runTest {
        val contact = ContactFactory.createContactEntity(firstName = "John")
        every {  localDataSource.observeById(1) } returns flowOf(contact)

        val result = repository.getContactStream("1").first()

        assertThat(result).isEqualTo(contact.toModel())
    }

    @Test
    fun `loadMoreData fetches data from network and inserts into localDataSource`() = runTest {
        val remoteContacts = listOf(
        ContactFactory.createContactDto("Jon")
    )
        coEvery { localDataSource.count() } returns 0
        coEvery { networkDataSource.loadContactList(1) } returns (remoteContacts)

        repository.loadMoreData()

        coVerify { networkDataSource.loadContactList(1) }
        coVerify { localDataSource.upsertAll(remoteContacts.toEntity()) }
    }

    @Test
    fun `loadMoreData calculates correct page number`() = runTest {
        val count = NetworkDataSourceImpl.RESULT_COUNT * 2
        coEvery { localDataSource.count() } returns count
        coEvery { networkDataSource.loadContactList(any()) } returns emptyList()

        repository.loadMoreData()

        coVerify { networkDataSource.loadContactList(3) }
    }
}