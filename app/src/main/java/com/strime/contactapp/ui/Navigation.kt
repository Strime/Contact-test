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

package com.strime.contactapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.strime.contactapp.ui.ContactDestinationsArgs.CONTACT_ID_ARG
import com.strime.contactapp.ui.ContactScreens.CONTACTS_SCREEN
import com.strime.contactapp.ui.ContactScreens.CONTACT_DETAIL_SCREEN
import com.strime.contactapp.ui.contactdetail.ContactDetailScreen
import com.strime.contactapp.ui.contacts.ContactsScreen

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ContactDestinations.CONTACTS_ROUTE,
    navActions: ContactNavigationActions = remember(navController) {
        ContactNavigationActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ContactDestinations.CONTACTS_ROUTE) {
            ContactsScreen(
                onContactClick = { contact -> navActions.navigateToContactDetail(contact.id) },
            )
        }
        composable(ContactDestinations.CONTACT_DETAIL_ROUTE) {
            ContactDetailScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Screens used in [ContactDestinations]
 */
private object ContactScreens {
    const val CONTACTS_SCREEN = "contacts"
    const val CONTACT_DETAIL_SCREEN = "contact"
}

/**
 * Arguments used in [ContactDestinations] routes
 */
object ContactDestinationsArgs {
    const val CONTACT_ID_ARG = "contactId"
}

/**
 * Destinations used in the [MainActivity]
 */
object ContactDestinations {
    const val CONTACTS_ROUTE = CONTACTS_SCREEN
    const val CONTACT_DETAIL_ROUTE = "$CONTACT_DETAIL_SCREEN/{$CONTACT_ID_ARG}"
}

/**
 * Models the navigation actions in the app.
 */
class ContactNavigationActions(private val navController: NavHostController) {
    fun navigateToContactDetail(contactId: Long) {
        navController.navigate("$CONTACT_DETAIL_SCREEN/$contactId")
    }
}