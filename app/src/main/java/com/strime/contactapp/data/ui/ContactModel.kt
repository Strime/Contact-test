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

package com.strime.contactapp.data.ui

import java.util.Locale

/**
 * Immutable model class for a Contact
 *
 * @property id
 * @property gender
 * @property title
 * @property firstName
 * @property lastName
 * @property email
 * @property phone
 * @property cell
 * @property city
 * @property country
 * @property pictureUrl
 * @property nat
 * @property uuid
 */
data class ContactModel(
    val id: Long,
    val gender: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    var phone: String,
    var cell: String,
    var city: String,
    var country: String,
    var pictureUrl: String,
    var nat: String,
    var uuid: String,
) {

    val name: String
        get() = "$firstName $lastName"
    val nameWithTitle: String
        get() = "$title. $firstName $lastName"
    val address: String
        get() = "$city, $country"


    val countryFlag: String
        get() = nat.uppercase(Locale.getDefault()).map { char -> 0x1F1E6 - 'A'.code + char.code }
            .joinToString("") { codePoint -> String(Character.toChars(codePoint)) }

}
