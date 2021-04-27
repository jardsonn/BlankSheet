package com.jcs.blanksheet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jcs.blanksheet.entity.Document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Jardson Costa on 22/03/2021.
 */

@Database(entities = [Document::class], version = 1)
abstract class DocumentDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao

    companion object {
        private const val DATABASE_NAME = "jotter_mark_db"

        @Volatile
        private var INSTANCE: DocumentDatabase? = null

        //@InternalCoroutinesApi
        fun getDatabase(context: Context, scope: CoroutineScope): DocumentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DocumentDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DocumentDatabaseCallBack(scope))
                    .build()
                INSTANCE = instance
                instance
            }


//            if (INSTANCE == null) {
//                synchronized(DocumentDatabase::class) {
//                    INSTANCE = dbBuild(context)
//                }
//            }
//            return INSTANCE as DocumentDatabase
        }

//        private fun dbRoomBuild(context: Context, scope: CoroutineScope): DocumentDatabase {
//            return Room.databaseBuilder(
//                context,
//                DocumentDatabase::class.java,
//                DATABASE_NAME
//            )
//                .fallbackToDestructiveMigration()
//                .addCallback(DocumentDatabaseCallBack(scope))
//                .build()
//        }
    }

    private class DocumentDatabaseCallBack(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                scope.launch { Dispatchers.IO }
            }
        }
    }
}