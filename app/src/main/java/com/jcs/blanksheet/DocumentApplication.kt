package com.jcs.blanksheet

import android.app.Application
import com.jcs.blanksheet.db.DocumentDatabase
import com.jcs.blanksheet.repository.DocumentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

/**
 * Created by Jardson Costa on 13/04/2021.
 */

class DocumentApplication: Application() {
    private val appScope = CoroutineScope(SupervisorJob())

    private val database by lazy { DocumentDatabase.getDatabase(this, appScope) }
    val repository by lazy { DocumentRepository(database.documentDao()) }
}