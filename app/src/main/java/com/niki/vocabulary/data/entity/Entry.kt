package com.niki.vocabulary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "definition") val definition: String
) {
    constructor(name: String, definition: String) : this(0, name, definition)
}