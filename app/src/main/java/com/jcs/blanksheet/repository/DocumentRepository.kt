package com.jcs.blanksheet.repository

import androidx.annotation.WorkerThread
import com.jcs.blanksheet.db.DocumentDao
import com.jcs.blanksheet.entity.Document
import com.jcs.blanksheet.utils.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Created by Jardson Costa on 12/04/2021.
 */

class DocumentRepository(private val dao: DocumentDao) {
    
    val documents: Flow<List<Document>> = dao.allDocuments()
    
    @WorkerThread
    fun getDocumentById(documentId: Long): Flow<Document> {
        return dao.getDocumentById(documentId)
    }
    
    @WorkerThread
    fun getDocuments(sort: String): Flow<List<Document>> {
        return when (sort) {
            Sort.get(0) -> {
                dao.getDocumentByNameAsc()
            }
            Sort.get(1) -> {
                dao.getDocumentByNameDesc()
            }
            Sort.get(2) -> {
                dao.getDocumentByDateAsc()
            }
            Sort.get(3) -> {
                dao.getDocumentByDateDesc()
            }
            else -> {
                dao.allDocuments()
            }
        }
    }
    
    @WorkerThread
    fun getSearchResult(query: String): Flow<List<Document>> {
        return dao.searchItem(query)
    }
    
//    @WorkerThread
//    suspend fun saveDocument(document: Document) = withContext(Dispatchers.IO) {
//        return@withContext dao.saveDocument(document)
//    }
    
    @WorkerThread
    suspend fun saveDocument(document: Document): Long {
        var id: Long
        withContext(Dispatchers.IO) {
            id = dao.saveDocument(document)
            println("repository: $id")
        }
        println("repository_2: $id")
        return id
    }
    
    @WorkerThread
    suspend fun updateDocument(document: Document) = withContext(Dispatchers.IO) {
        dao.updateDocument(document)
    }
    
    @WorkerThread
    suspend fun deleteDocument(vararg document: Document) = withContext(Dispatchers.IO) {
        dao.deleteDocument(*document)
    }
    
    @WorkerThread
    suspend fun deleteDocumentById(documentId: Long) = withContext(Dispatchers.IO) {
        dao.deleteDocumentById(documentId)
    }
    
}