package com.example.simplenotes.room

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simplenotes.Notes
import com.example.simplenotes.SingletonHolder

@Database(version = 1, entities = [Notes::class])
abstract class NotesDatabase : RoomDatabase(){

    // Singleton NotesDatabase
    // https://android--code.blogspot.com/2019/02/android-kotlin-room-singleton-example.html
    // .allowMainThreadQueries when building can be used to override main thread restriction
    companion object {
        private var INSTANCE: NotesDatabase? = null
        fun getInstance(context: Context): NotesDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    NotesDatabase::class.java,
                    "roomdb"
                )
                    .build()
            }

            return INSTANCE as NotesDatabase
        }
    }

//    https://stackoverflow.com/questions/45912619/using-room-as-singleton-in-kotlin
//    companion object SingletonHolder<NotesDatabase, Context> ({
//        Room.databaseBuilder(application, NotesDatabase::class.java, "notes").build()
//    })

//    companion object {
//        fun get(application: Application) : NotesDatabase {
//            return Room.databaseBuilder(application, NotesDatabase::class.java, "notes")
//                .build()
//        }
//    }

    abstract fun getNotesDao(): NotesDao
}