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

package com.strime.contactapp.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.strime.contactapp.data.local.database.AppDatabase
import com.strime.sharedtestcode.data.ContactFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ContactDaoTest {

    // using an in-memory database because the information stored here disappears when the
    // process is killed
    private lateinit var database: AppDatabase

    // Ensure that we use a new database for each test.
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @Test
    fun insertContactAndGetById() = runTest {
        // Given
        val contact = ContactFactory.createContactEntity("Jon")
        database.contactDao().upsertAll(contacts = listOf(contact))

        // When
        val loaded = database.contactDao().observeById(1).first()

        // Then
        assertNotNull(loaded)
        assertEquals(contact.title, loaded.title)
        assertEquals(contact.firstName, loaded.firstName)
    }

    @Test
    fun insertContactsAndGetAll() = runTest {
        // Given
        val contact1 = ContactFactory.createContactEntity("Jon")
        val contact2 = ContactFactory.createContactEntity("Jack")
        val contact3 = ContactFactory.createContactEntity("Jane")
        database.contactDao().upsertAll(contacts = listOf(contact1, contact3, contact2))

        // When
        val loaded = database.contactDao().observeAll().first()

        // Then
        assertNotNull(loaded)
        assertEquals(loaded.size, 3)
        assertEquals(loaded[0].firstName, contact1.firstName)
        assertEquals(loaded[1].firstName, contact3.firstName)
        assertEquals(loaded[2].firstName, contact2.firstName)
    }
}
