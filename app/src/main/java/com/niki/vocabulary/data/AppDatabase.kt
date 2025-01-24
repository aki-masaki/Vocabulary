package com.niki.vocabulary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.niki.vocabulary.data.dao.EntryDao
import com.niki.vocabulary.data.dao.LikeDao
import com.niki.vocabulary.data.entity.Entry
import com.niki.vocabulary.data.entity.Like

@Database(entities = [Entry::class, Like::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun likeDao(): LikeDao
}