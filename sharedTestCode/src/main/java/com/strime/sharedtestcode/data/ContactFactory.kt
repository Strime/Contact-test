package com.strime.sharedtestcode.data

import com.strime.contactapp.data.local.database.ContactEntity
import com.strime.contactapp.data.ui.ContactModel
import java.util.UUID

class ContactFactory {

    companion object {
        fun createContactModel(
            id: Long,
            firstName: String,
            gender: String = "male",
            title: String = "Mr",
            lastName: String = "Doe",
            phone: String = "0123456789",
            cell: String = "0123456789",
            city: String = "Paris",
            country: String = "France",
            pictureUrl: String = "",
            nat: String = "FR",
            uuid: String = UUID.randomUUID().toString()
        ): ContactModel {
            return ContactModel(
                id,
                firstName = firstName,
                gender = gender,
                title = title,
                lastName = lastName,
                phone = phone,
                cell = cell,
                city = city,
                email = "$firstName$lastName@test.com",
                country = country,
                pictureUrl = pictureUrl,
                nat = nat,
                uuid = uuid,
            )
        }
        fun createContactEntity(
            firstName: String,
            gender: String = "male",
            title: String = "Mr",
            lastName: String = "Doe",
            phone: String = "0123456789",
            cell: String = "0123456789",
            city: String = "Paris",
            country: String = "France",
            pictureUrl: String = "",
            nat: String = "FR",
            uuid: String = UUID.randomUUID().toString()
        ): ContactEntity {
            return ContactEntity(
                0,
                firstName = firstName,
                gender = gender,
                title = title,
                lastName = lastName,
                phone = phone,
                cell = cell,
                city = city,
                email = "$firstName$lastName@test.com",
                country = country,
                pictureUrl = pictureUrl,
                nat = nat,
                uuid = uuid,
            )
        }
    }

}