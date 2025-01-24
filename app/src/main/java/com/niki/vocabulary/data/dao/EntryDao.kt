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

    @Insert
    suspend fun insert(vararg entry: Entry)
}