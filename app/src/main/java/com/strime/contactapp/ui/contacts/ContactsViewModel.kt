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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.strime.contactapp.ui.contacts

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strime.contactapp.R
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.ui.util.Async
import com.strime.contactapp.ui.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the task list screen.
 */
data class ContactsUiState(
    val isLoading: Boolean = false,
    val items: List<ContactModel> = emptyList(),
    val networkFailed: Boolean = false,
    val userMessage: Int? = null
)

/**
 * ViewModel for the task list screen.
 */
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
) : ViewModel() {

    @VisibleForTesting
    val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)

    private val _fetchingState: MutableStateFlow<Async<List<ContactModel>>> = MutableStateFlow(Async.Uninitialized)

    private val _contactsAsync = contactRepository.getContactsStream()

    val uiState: StateFlow<ContactsUiState> = combine(
        _isLoading, _userMessage, _contactsAsync, _fetchingState
    ) { isLoading, userMessage, contacts, fetchingState ->
        when (fetchingState) {
            is Async.Uninitialized -> {
                if (contacts.isEmpty()) {
                    loadMoreContact()
                }
                ContactsUiState(
                    isLoading = isLoading,
                    items = contacts,
                    networkFailed = false,
                    userMessage = userMessage
                )
            }
            is Async.Error -> {
                ContactsUiState(
                    isLoading = isLoading,
                    items = contacts,
                    networkFailed = true,
                    userMessage = userMessage
                )
            }
            is Async.Success -> {
                ContactsUiState(
                    isLoading = isLoading,
                    items = contacts,
                    networkFailed = false,
                    userMessage = userMessage,
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = ContactsUiState(isLoading = true)
        )


    fun loadMoreContact() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                contactRepository.loadMoreData()
                _fetchingState.value = Async.Success
            } catch (e: Exception) {
                _userMessage.value = R.string.loading_contact_error
                _fetchingState.value = Async.Error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun snackbarMessageShown() {
        _userMessage.value = null
    }
}