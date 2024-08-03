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

package com.strime.contactapp.data.local.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the contact table.
 */
@Dao
interface ContactDao {

    /**
     * Observes list of contacts.
     *
     * @return all contacts.
     */
    @Query("SELECT * FROM contact ORDER BY ID")
    fun observeAll(): Flow<List<ContactEntity>>

    /**
     * Observes a single contact.
     *
     * @param contactId the contact id.
     * @return the contact with contactId.
     */
    @Query("SELECT * FROM contact WHERE id = :contactId")
    fun observeById(contactId: Long): Flow<ContactEntity>

    /**
     * Count all contacts from the contacts table.
     *
     * @return all contacts.
     */
    @Query("SELECT COUNT(id) FROM contact")
    suspend fun count(): Int

    /**
     * Insert or update contacts in the database. If a contact already exists, replace it.
     *
     * @param contacts the contacts to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(contacts: List<ContactEntity>)

    /**
     * Delete all contacts.
     */
    @Query("DELETE FROM contact")
    suspend fun deleteAll()
}
