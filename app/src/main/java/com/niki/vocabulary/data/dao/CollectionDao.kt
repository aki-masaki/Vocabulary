package com.niki.vocabulary.data.dao

import androidx.room.Dao
import androidx.room.Delete
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

    @Transaction
    @Query("SELECT * FROM Collection INNER JOIN CollectionEntryCrossRef ON Collection.id = CollectionEntryCrossRef.collectionId WHERE CollectionEntryCrossRef.entryId = :entryId")
    suspend fun getByEntryId(entryId: Int): List<CollectionWithEntries>

    @Insert
    suspend fun insert(collection: Collection): Long

    @Insert
    suspend fun insertCollectionEntryCrossRef(crossRef: CollectionEntryCrossRef)

    @Delete
    suspend fun deleteCollectionEntryCrossRef(crossRef: CollectionEntryCrossRef)

    @Query(
        "SELECT * FROM CollectionEntryCrossRef WHERE entryId = :entryId AND collectionId = :collectionId"
    )
    suspend fun getCollectionEntryCrossRef(
        entryId: Int,
        collectionId: Int
    ): CollectionEntryCrossRef?

    @Query("SELECT * FROM CollectionEntryCrossRef WHERE collectionId = :id")
    suspend fun getCrossRefByCollectionId(id: Int): List<CollectionEntryCrossRef>
}