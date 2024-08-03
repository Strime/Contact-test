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

package com.strime.contactapp.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strime.contactapp.R
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.ui.util.Async
import com.strime.contactapp.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * UiState for the task list screen.
 */
data class ContactsUiState(
    val items: List<ContactModel> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

/**
 * ViewModel for the task list screen.
 */
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : ViewModel() {

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _filteredContactsAsync =
        contactRepository.getContactsStream()
            .map {
                if (it.isEmpty()) {
                    contactRepository.loadMoreData()
                }
                Async.Success(it)
            }
            .catch<Async<List<ContactModel>>> {
                emit(Async.Error(R.string.loading_contacts_error))
            }

    val uiState: StateFlow<ContactsUiState> = combine(
        _isLoading, _userMessage, _filteredContactsAsync
    ) { isLoading, userMessage, contactsAsync ->
        when (contactsAsync) {
            Async.Loading -> {
                ContactsUiState(isLoading = true)
            }
            is Async.Error -> {
                ContactsUiState(userMessage = contactsAsync.errorMessage)
            }
            is Async.Success -> {
                ContactsUiState(
                    items = contactsAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = ContactsUiState(isLoading = true)
        )


    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

}


data class UiInfo(
    val currentFilteringLabel: Int = R.string.label_all,
    val noTasksLabel: Int = R.string.no_contacts_all,
    val noTaskIconRes: Int = R.drawable.logo_no_fill,
)
