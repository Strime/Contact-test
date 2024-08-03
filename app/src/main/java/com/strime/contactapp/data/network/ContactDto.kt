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

data class RandomUserResultDto(
    val results: List<ContactDto>,
)
data class ContactDto(
    val login: LoginDto,
    val gender: String,
    val email: String,
    val name: ContactNameDto,
    val phone: String,
    val cell: String,
    val location: LocationDto,
    val picture: PictureDto,
    val nat: String,
)

data class LocationDto(
    val city: String,
    val country: String,
)

data class LoginDto(
    val uuid: String,
)

data class PictureDto(
    val medium: String,
    val large: String,
)

data class ContactNameDto(
    val title: String,
    val first: String,
    val last: String
)

