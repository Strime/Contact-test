/*
 * Copyright 2023 The Android Open Source Project
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

package com.strime.contactapp.data

import com.strime.contactapp.data.network.ContactDto
import com.strime.contactapp.data.network.ContactNameDto
import com.strime.contactapp.data.network.LocationDto
import com.strime.contactapp.data.network.LoginDto
import com.strime.contactapp.data.network.PictureDto
import com.strime.contactapp.data.local.database.ContactEntity
import com.strime.contactapp.data.ui.ContactModel

/**
 * Data model mapping extension functions. There are three model types:
 *
 * - ContactModel: External model exposed to other layers in the architecture.
 * Obtained using `toModel`.
 *
 * - ContactDto: Internal model used to represent a task from the network. Obtained using
 * `toNetwork`.
 *
 * - ContactEntity: Internal model used to represent a task stored locally in a database. Obtained
 * using `toEntity`.
 *
 */

// Entity to Model
fun ContactEntity.toModel() = ContactModel(
    id = id,
    gender = gender,
    title = title,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    cell = cell,
    pictureUrl = pictureUrl,
    uuid = uuid,
    nat = nat,
    country = country,
    city = city
)
@JvmName("entityToModel")
fun List<ContactEntity>.toModel() = map(ContactEntity::toModel)

// Network to Entity
fun ContactDto.toEntity() = ContactEntity(
    gender = gender,
    title = name.title,
    firstName = name.first,
    lastName = name.last,
    email = email,
    phone = phone,
    cell = cell,
    pictureUrl = picture.large,
    nat = nat,
    uuid = login.uuid,
    city = location.city,
    country = location.country,
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("dtoToLocal")
fun List<ContactDto>.toEntity() = map(ContactDto::toEntity)


fun ContactModel.toEntity() = ContactEntity(
    id = id,
    gender = gender,
    title = title,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    cell = cell,
    pictureUrl = pictureUrl,
    uuid = uuid,
    nat = nat,
    country = country,
    city = city
)

@JvmName("modelToEntity")
fun List<ContactModel>.toEntity() = map(ContactModel::toEntity)


fun ContactModel.toDto() = ContactDto(
    gender = gender,
    name = ContactNameDto(
        title = title,
        first = firstName,
        last = lastName
    ),
    email = email,
    phone = phone,
    cell = cell,
    picture = PictureDto(
        large = pictureUrl,
        medium = "", // Assuming medium and thumbnail are not stored in model
    ),
    nat = nat,
    login = LoginDto(
        uuid = uuid,
    ),
    location = LocationDto(
        city = city,
        country = country,
    )
)

@JvmName("modelToDto")
fun List<ContactModel>.toDto() = map(ContactModel::toDto)
