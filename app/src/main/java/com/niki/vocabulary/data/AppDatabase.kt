package com.niki.vocabulary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.niki.vocabulary.data.dao.EntryDao
import com.niki.vocabulary.data.entity.Entry

@Database(entities = [Entry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
}