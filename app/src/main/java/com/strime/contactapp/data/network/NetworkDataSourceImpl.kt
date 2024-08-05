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

package com.strime.contactapp.data.network

import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val networkContactService: NetworkContactService
) : NetworkDataSource {
    companion object {
        const val SEED = "lydia"
        const val RESULT_COUNT = 20
    }


    override suspend fun loadContactList(page: Int): List<ContactDto> {
        val res =
            networkContactService.getContactList(seed = SEED, page = page, results = RESULT_COUNT)
        return res.body()?.results ?: emptyList()

    }
}

