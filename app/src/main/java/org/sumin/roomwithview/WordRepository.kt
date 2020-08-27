package org.sumin.roomwithview

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
// 쿼리를 관리하고 여러 백엔드를 사용할수 있도록하며 데이터를 네트워크에서 가져올지 로컬 데이터베이스에
// 캐시 된 결과를 사용할지 결정하기 위한 논리를 구현한다.
class WordRepository(private val wordDao: WordDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<Word>> = wordDao.getAlphabetizedWords()

    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
}