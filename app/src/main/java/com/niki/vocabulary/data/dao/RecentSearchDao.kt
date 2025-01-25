package com.niki.vocabulary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.niki.vocabulary.data.entity.RecentSearch

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recentsearch")
    suspend fun getAll(): List<RecentSearch>

    @Insert
    suspend fun insert(search: RecentSearch)

    @Delete
    suspend fun delete(search: RecentSearch)

    @Query("DELETE FROM recentsearch")
    suspend fun deleteAll()
}