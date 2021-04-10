package com.jcs.blanksheet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jcs.blanksheet.model.Document
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

/**
 * Created by Jardson Costa on 22/03/2021.
 */

@Database(entities = [Document::class], version = 1)
abstract class DocumentDatabase : RoomDatabase() {
   abstract fun documentDao(): DocumentDao

    companion object {
        private const val DATABASE_NAME = "jotter_mark_db"
        private var instance: DocumentDatabase? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context?): DocumentDatabase {
            if (instance == null) {
                synchronized(DocumentDatabase::class){
                    instance = dbBuild(context)
                }
            }
            return instance as DocumentDatabase
        }

        private fun dbBuild(context: Context?): DocumentDatabase {
          return  Room.databaseBuilder(
                context!!,
                DocumentDatabase::class.java,
                DATABASE_NAME
            )
                .allowMainThreadQueries()
                .build()
        }
    }


//        fun getInstance(context: Context?): DocumentDatabase {
//            if (instance == null) instance =
//                Room.databaseBuilder(
//                    context!!,
//                    DocumentDatabase::class.java,
//                    DATABASE_NAME
//                )
//                    .allowMainThreadQueries()
//                    .build()
//            return instance as DocumentDatabase
//        }
//    }

    /*
    *  fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "mindorks-example-coroutines"
        ).build()
        * */
}