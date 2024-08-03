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

import com.strime.contactapp.data.di.ApplicationScope
import com.strime.contactapp.data.di.DefaultDispatcher
import com.strime.contactapp.data.local.database.ContactDao
import com.strime.contactapp.data.network.NetworkDataSource
import com.strime.contactapp.data.network.NetworkDataSourceImpl
import com.strime.contactapp.data.ui.ContactModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of [ContactRepository]. Single entry point for managing account's data.
 *
 * @param networkDataSource - The network data source
 * @param localDataSource - The local data source
 * @param dispatcher - The dispatcher to be used for long running or complex operations, such as ID
 * generation or mapping many models.
 * @param scope - The coroutine scope used for deferred jobs where the result isn't important, such
 * as sending data to the network.
 */
@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: ContactDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : ContactRepository {

    override fun getContactsStream(): Flow<List<ContactModel>> {
        return localDataSource.observeAll()
            .map { contacts -> contacts.toModel() }
            .flowOn(dispatcher)

    }

    override fun getContactStream(contactId: String): Flow<ContactModel?> {
        return localDataSource.observeById(contactId.toLong())
            .map { contact -> contact.toModel() }
            .flowOn(dispatcher)
    }


    /**
     * Loads more data from the network and stores it in the local data source.
     * This function calculates the current page number based on the number of items in the local data source and the number of results per page.
     * It then fetches the next page of data from the network data source and inserts or updates the data in the local data source.
     */
    override suspend fun loadMoreData() {
        withContext(dispatcher) {
            val pageCount = localDataSource.count() / NetworkDataSourceImpl.RESULT_COUNT
            val remoteContacts = networkDataSource.loadContactList(pageCount + 1)
            localDataSource.upsertAll(remoteContacts.toEntity())
        }
    }
}
