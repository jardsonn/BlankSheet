package com.jcs.blanksheet.db

import androidx.room.*
import com.jcs.blanksheet.model.Document

/**
 * Created by Jardson Costa on 22/03/2021.
 */

@Dao
interface DocumentDao {

    /**
     * Salavar o documento no banco de dados
     *
     * @param document
     **/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDocument(document: Document): Long

    /**
     * Atualizar o documento
     *
     * @param document
     **/
    @Update
    fun updateDocument(document: Document)

    /**
     * Excluir o documento
     *
     * @param document
     **/
    @Delete
    fun deleteDocument(vararg document: Document)

    /**
     * Listar todos os documentos do banco de dados
     **/
    @get:Query("SELECT * FROM main_table")
    val allDocuments: List<Document>

//    @Query("SELECT * FROM main_table")
//    fun allDocuments(): Flow<List<Document>>

    /**
     * Obtem o documento de acordo com seu id
     *
     * @param documentId
     * @return JotterMarkDocument
     **/
    @Query("SELECT * FROM main_table WHERE id = :documentId")
    fun getDocumentById(documentId: Int): Document

// @Query("SELECT * FROM main_table WHERE id = :documentId")
//    fun getDocumentById(documentId: Int): Flow<Document>



    /**
     * Exclui o documento de acordo com seu id
     *
     * @param documentId
     **/
    @Query("DELETE FROM main_table WHERE id = :documentId")
    fun deleteDocumentById(documentId: Int)
}