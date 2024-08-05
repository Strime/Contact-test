package com.strime.contactapp.ui.contactdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.ui.ContactDestinationsArgs
import com.strime.contactapp.ui.contacts.ContactsViewModel
import com.strime.sharedtestcode.MainCoroutineRule
import com.strime.sharedtestcode.data.ContactFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactDetailViewModelTest{


    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()


    private lateinit var contactRepository: ContactRepository
    private lateinit var viewModel: ContactDetailViewModel

    @Test
    fun `init  correctly`() = runTest {
        // Given
        val contactId = "12"
        contactRepository = mockk<ContactRepository>()
        val savedStateHandle = SavedStateHandle().apply {
            set(ContactDestinationsArgs.CONTACT_ID_ARG, contactId)
        }

        val contact = ContactFactory.createContactModel(id = 12, firstName = "Jon")
        every { contactRepository.getContactStream(any()) } returns  flow { emit(contact) }
        viewModel = ContactDetailViewModel(contactRepository, savedStateHandle)


        // Then
        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.isLoading).isFalse()
            assertThat(uiState.contactModel).isEqualTo(contact)
            assertThat(uiState.userMessage).isNull()
        }
    }
}