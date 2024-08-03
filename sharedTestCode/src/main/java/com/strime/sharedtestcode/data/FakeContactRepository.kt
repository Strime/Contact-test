package com.strime.sharedtestcode.data

import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.data.ui.ContactModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.VisibleForTesting

class FakeContactRepository: ContactRepository {

    private var shouldThrowError = false

    private val _savedTasks = MutableStateFlow(LinkedHashMap<Long, ContactModel>())
    val savedContacts: StateFlow<LinkedHashMap<Long, ContactModel>> = _savedTasks.asStateFlow()

    private val observableContacts: Flow<List<ContactModel>> = savedContacts.map {
        if (shouldThrowError) {
            throw Exception("Test exception")
        } else {
            it.values.toList()
        }
    }

    fun setShouldThrowError(value: Boolean) {
        shouldThrowError = value
    }


    override fun getContactsStream(): Flow<List<ContactModel>> {
       return observableContacts
    }

    override fun getContactStream(contactId: String): Flow<ContactModel?> {
        if (shouldThrowError) {
            throw Exception("Test exception")
        }
        return flowOf(savedContacts.value[contactId.toLong()])

    }

    override suspend fun loadMoreData() {
        //
    }


    @VisibleForTesting
    fun addTasks(vararg contacts: ContactModel) {
        _savedTasks.update { oldContacts ->
            val newTasks = LinkedHashMap<Long, ContactModel>(oldContacts)
            for (contact in contacts) {
                newTasks[contact.id] = contact
            }
            newTasks
        }
    }

}