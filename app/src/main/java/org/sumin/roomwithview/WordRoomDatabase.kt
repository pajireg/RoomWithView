package org.sumin.roomwithview

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@Database(entities = arrayOf(Word::class), version = 1, exportSchema = false)
public abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context, scope: CoroutineScope
        ): WordRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
//                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.wordDao())
                    }
                }
            }
            /**
             * Populate the database in a new coroutine.
             * If you want to start with more words, just add them.
             */
            suspend fun populateDatabase(wordDao: WordDao) {
                // Start the app with a clean database every time.
                // Not needed if you only populate on creation.
                // Delete all content here.
                wordDao.deleteAll()

                // Add sample words.
                var word = Word("Hello")
                wordDao.insert(word)

                word = Word("World!")
                wordDao.insert(word)

                word = Word("TODO!")
                wordDao.insert(word)
            }
        }
    }
}