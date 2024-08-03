package com.strime.contactapp.ui.contacts

import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.strime.contactapp.ui.contactdetail.ContactContent
import com.strime.sharedtestcode.data.ContactFactory
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for [ContactContent].
 */
@RunWith(AndroidJUnit4::class)
class ContactContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {

    }

    @Test
    fun withMultiplesModel() {
        // Given
        composeTestRule.setContent {
            ContactsContent(
                loading = false,
                contactModels = listOf(
                    ContactFactory.createContactModel(1, "Jon", phone = "0123456789"),
                    ContactFactory.createContactModel(2, "Jack", phone = "1234567890"),
                ),
                onContactClick = {},
                modifier = Modifier,
                listState = rememberLazyListState(),
            )
        }
        // When
        composeTestRule.onNodeWithText("Jon Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("0123456789").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jack Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("1234567890").assertIsDisplayed()
    }
}