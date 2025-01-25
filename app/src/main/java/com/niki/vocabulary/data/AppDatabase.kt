package com.niki.vocabulary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.niki.vocabulary.data.dao.CollectionDao
import com.niki.vocabulary.data.dao.EntryDao
import com.niki.vocabulary.data.dao.LikeDao
import com.niki.vocabulary.data.dao.RecentSearchDao
import com.niki.vocabulary.data.entity.Collection
import com.niki.vocabulary.data.entity.Entry
import com.niki.vocabulary.data.entity.Like
import com.niki.vocabulary.data.entity.RecentSearch
import com.niki.vocabulary.data.entity.relations.CollectionEntryCrossRef

@Database(
    entities = [Entry::class, Like::class, RecentSearch::class, Collection::class, CollectionEntryCrossRef::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun likeDao(): LikeDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun collectionDao(): CollectionDao
}