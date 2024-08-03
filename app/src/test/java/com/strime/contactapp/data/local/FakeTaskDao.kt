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

package com.strime.contactapp.data.local

import com.strime.contactapp.data.local.database.ContactDao
import com.strime.contactapp.data.local.database.ContactEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FakeContactDao(
    initialContacts: List<ContactEntity>? = mutableListOf()
) : ContactDao {

    private var _contacts: MutableMap<Long, ContactEntity>? = null

    private var contacts: List<ContactEntity>?
        get() = _contacts?.values?.toList()
        set(newContacts) {
            _contacts = newContacts?.associateBy { it.id }?.toMutableMap()
        }

    init {
        contacts = initialContacts
    }

    override fun observeAll(): Flow<List<ContactEntity>> {
        return  callbackFlow {
            contacts?.let { trySend(it) } ?: throw Exception("Contact list is null")
            close()
            awaitClose()
        }
    }

    override fun observeById(contactId: Long): Flow<ContactEntity> {
        return  callbackFlow {
            _contacts?.get(contactId)?.let { trySend(it) } ?: throw Exception("Contact list is null")
            close()
            awaitClose()
        }
    }

    override suspend fun count(): Int {
        return contacts?.size ?: 0
    }

    override suspend fun upsertAll(contacts: List<ContactEntity>) {
        _contacts?.putAll(contacts.associateBy { it.id })
    }

    override suspend fun deleteAll() {
        TODO("Not yet implemented")
    }
}