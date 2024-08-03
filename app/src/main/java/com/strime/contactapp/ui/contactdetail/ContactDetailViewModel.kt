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

package com.strime.contactapp.ui.contactdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strime.contactapp.R
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.data.ui.ContactModel
import com.strime.contactapp.ui.ContactDestinationsArgs
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
 * UiState for the Details screen.
 */
data class TaskDetailUiState(
    val contactModel: ContactModel? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
)

/**
 * ViewModel for the Details screen.
 */
@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    contactRepository: ContactRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: String = savedStateHandle[ContactDestinationsArgs.CONTACT_ID_ARG]!!

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _contactModelAsync = contactRepository.getContactStream(contactId)
        .map { handleContact(it) }
        .catch { emit(Async.Error(R.string.loading_contact_error)) }

    val uiState: StateFlow<TaskDetailUiState> = combine(
        _userMessage, _isLoading, _contactModelAsync
    ) { userMessage, isLoading, taskAsync ->
        when (taskAsync) {
            Async.Loading -> {
                TaskDetailUiState(isLoading = true)
            }
            is Async.Error -> {
                TaskDetailUiState(
                    userMessage = taskAsync.errorMessage
                )
            }
            is Async.Success -> {
                TaskDetailUiState(
                    contactModel = taskAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TaskDetailUiState(isLoading = true)
        )


    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackbarMessage(message: Int) {
        _userMessage.value = message
    }

    private fun handleContact(contactModel: ContactModel?): Async<ContactModel?> {
        if (contactModel == null) {
            return Async.Error(R.string.contact_not_found)
        }
        return Async.Success(contactModel)
    }
}
