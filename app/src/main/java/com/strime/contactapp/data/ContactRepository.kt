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

import com.strime.contactapp.data.ui.ContactModel
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer.
 */
interface ContactRepository {

    fun getContactsStream(): Flow<List<ContactModel>>

    fun getContactStream(contactId: String): Flow<ContactModel?>

    suspend fun loadMoreData()
}
