package com.niki.vocabulary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collection(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "iconName") val iconName: String,
    @ColumnInfo(name = "colorLong") val colorLong: Long
) {
    constructor(name: String, iconName: String, colorLong: Long) : this(
        0,
        name,
        iconName,
        colorLong
    )
}