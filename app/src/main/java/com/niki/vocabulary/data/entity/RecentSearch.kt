package com.niki.vocabulary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentSearch(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "entry_id") val entryId: Int
) {
    constructor(entryId: Int) : this(0, entryId)
}