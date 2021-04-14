package com.jcs.blanksheet.db

import androidx.room.*
import com.jcs.blanksheet.model.Document
import kotlinx.coroutines.flow.Flow

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
    suspend fun saveDocument(document: Document): Long

    /**
     * Atualizar o documento
     *
     * @param document
     **/
    @Update
    suspend fun updateDocument(document: Document)

    /**
     * Excluir o documento
     *
     * @param document
     **/
    @Delete
    suspend fun deleteDocument(vararg document: Document)

    /**
     * Exclui o documento de acordo com seu id
     *
     * @param documentId
     **/
    @Query("DELETE FROM main_table WHERE id = :documentId")
    suspend fun deleteDocumentById(documentId: Int)

    /**
     * Obtem o documento de acordo com seu id
     *
     * @param documentId
     * @return JotterMarkDocument
     **/
    @Query("SELECT * FROM main_table WHERE id = :documentId")
    fun getDocumentById(documentId: Int): Flow<Document>

    /**
     * Lista todos os documentos do banco de dados
     *
     **/
    @Query("SELECT * FROM main_table")
    fun allDocuments(): Flow<List<Document>>

    /**
     * Lista todos os documentos do banco de dados ordenda por titulo.
     * ORDENAR de A a Z
     **/
    @Query("SELECT * FROM main_table ORDER BY title ASC")
    fun getDocumentByNameAsc(): Flow<List<Document>>

    /**
     * Lista todos os documentos do banco de dados ordenda por titulo.
     * ORDENAR de Z a A
     **/
    @Query("SELECT * FROM main_table ORDER BY title DESC")
    fun getDocumentByNameDesc(): Flow<List<Document>>

    /**
     * Lista todos os documentos do banco de dados ordenda por data.
     * ORDENAR do mais recente ao mais antigo
     **/
    @Query("SELECT * FROM main_table ORDER BY dateForOrder ASC")
    fun getDocumentByDateAsc(): Flow<List<Document>>

    /**
     * Lista todos os documentos do banco de dados ordenda por data.
     * ORDENAR do mais antigo ao mais recente
     **/
    @Query("SELECT * FROM main_table ORDER BY dateForOrder DESC")
    fun getDocumentByDateDesc(): Flow<List<Document>>

}