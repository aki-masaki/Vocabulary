package com.niki.vocabulary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.niki.vocabulary.data.entity.Entry

@Dao
interface EntryDao {
    @Query("SELECT * FROM entry LIMIT 1")
    suspend fun getFirst(): Entry

    @Query("SELECT COUNT(*) FROM Entry")
    suspend fun getCount(): Int

    @Query("SELECT * FROM entry ORDER BY RANDOM() LIMIT 50")
    suspend fun getRandomList(): List<Entry>

    @Query("SELECT * FROM entry WHERE word LIKE :word")
    suspend fun getFromWord(word: String): List<Entry>

    @Query("SELECT * FROM entry WHERE id = :id")
    suspend fun getFromId(id: Int): Entry

    @Insert
    suspend fun insert(vararg entry: Entry)
}