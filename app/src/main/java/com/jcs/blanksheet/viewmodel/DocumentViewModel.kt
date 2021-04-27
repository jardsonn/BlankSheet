package com.jcs.blanksheet.viewmodel

import androidx.lifecycle.*
import com.jcs.blanksheet.entity.Document
import com.jcs.blanksheet.repository.DocumentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Created by Jardson Costa on 12/04/2021.
 */

class DocumentViewModel(private val repository: DocumentRepository) : ViewModel() {
    
    val currentId = MutableLiveData<Long>()
    
    val allDocuments: LiveData<List<Document>>
        get() = repository.documents.flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)
    
    fun getAllDocuments(sort: String): LiveData<List<Document>> =
        repository.getDocuments(sort).flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)
    
    fun searchDocument(query: String): LiveData<List<Document>> =
        repository.getSearchResult(query).flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)
    
    fun getDocumentById(documentId: Long): LiveData<Document> =
        repository.getDocumentById(documentId).flowOn(Dispatchers.Main)
            .asLiveData(context = viewModelScope.coroutineContext)
    
    fun saveDocument(document: Document) = viewModelScope.launch {
        currentId.value = repository.saveDocument(document)
    }
    
    
    fun saveOrUpdate(document: Document): Long {
        var id: Long = 0L
        viewModelScope.launch {
            if (document.id == 0L)
                id = repository.saveDocument(document)
            else {
                id = 0
                repository.updateDocument(document)
            }
        }
        return id
    }
    
    
    fun updateDocument(document: Document) = viewModelScope.launch {
        repository.updateDocument(document)
    }
    
    fun deleteDocument(vararg document: Document) = viewModelScope.launch {
        repository.deleteDocument(*document)
    }
    
    fun deleteDocumentById(documentId: Long) = viewModelScope.launch {
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