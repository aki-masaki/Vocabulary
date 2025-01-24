package com.niki.vocabulary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.niki.vocabulary.data.entity.Like

@Dao
interface LikeDao {
    @Query("SELECT * FROM `like`")
    suspend fun getAll(): List<Like>

    @Query("SELECT COUNT(*) FROM `like`")
    suspend fun getCount(): Int

    @Query("SELECT * FROM `like` WHERE entry_id = :entryId")
    fun getByEntryId(entryId: Int): Like?

    @Insert
    suspend fun insert(like: Like)

    @Delete
    suspend fun delete(like: Like)
}