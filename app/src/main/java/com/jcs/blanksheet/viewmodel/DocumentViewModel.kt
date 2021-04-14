package com.jcs.blanksheet.viewmodel

import androidx.lifecycle.*
import com.jcs.blanksheet.model.Document
import com.jcs.blanksheet.repository.DocumentRepository
import com.jcs.blanksheet.utils.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Created by Jardson Costa on 12/04/2021.
 */

class DocumentViewModel(private val repository: DocumentRepository) : ViewModel() {

    val allDocuments: LiveData<List<Document>>
        get() = repository.documents.flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun getDocumentById(documentId: Int): LiveData<Document> =
        repository.getDocumentById(documentId).flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun getAllDocuments(sort: String): LiveData<List<Document>> =
        repository.getDocuments(sort).flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)

    fun saveDocument(document: Document) = viewModelScope.launch {
        repository.saveDocument(document)
    }

    fun updateDocument(document: Document) = viewModelScope.launch {
        repository.updateDocument(document)
    }

    fun deleteDocument(vararg document: Document) = viewModelScope.launch {
        repository.deleteDocument(*document)
    }

    fun deleteDocumentById(documentId: Int) = viewModelScope.launch {
        repository.deleteDocumentById(documentId)
    }


//fun getDocumentById(documentId: Int) = viewModelScope.launch {
//        repository.getDocumentById(documentId)
//    }
//

    class DocViewModelFactory(private val repository: DocumentRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DocumentViewModel::class.java)) {
                return DocumentViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}