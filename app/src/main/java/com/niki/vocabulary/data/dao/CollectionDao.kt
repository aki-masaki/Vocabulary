package com.niki.vocabulary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.niki.vocabulary.data.entity.Collection
import com.niki.vocabulary.data.entity.relations.CollectionEntryCrossRef
import com.niki.vocabulary.data.entity.relations.CollectionWithEntries

@Dao
interface CollectionDao {
    @Query("SELECT * FROM Collection")
    suspend fun getAll(): List<Collection>

    @Transaction
    @Query("SELECT * FROM Collection WHERE id = :id")
    suspend fun getById(id: Int): CollectionWithEntries

    @Insert
    suspend fun insert(collection: Collection): Long

    @Insert
    suspend fun insertCollectionEntryCrossRef(crossRef: CollectionEntryCrossRef)
}