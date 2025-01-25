package com.niki.vocabulary.data.entity.relations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niki.vocabulary.data.entity.Collection
import com.niki.vocabulary.data.entity.Entry

@Entity(
    primaryKeys = ["entryId", "collectionId"], foreignKeys = [ForeignKey(
        entity = Entry::class, parentColumns = ["id"], childColumns = ["entryId"]
    ), ForeignKey(
        entity = Collection::class,
        parentColumns = ["id"],
        childColumns = ["collectionId"]
    )]
)
data class CollectionEntryCrossRef(
    val entryId: Int, val collectionId: Int
)

data class CollectionWithEntries(
    @Embedded val collection: Collection, @Relation(
        parentColumn = "id", entityColumn = "id", associateBy = Junction(
            CollectionEntryCrossRef::class, parentColumn = "collectionId", entityColumn = "entryId"
        )
    ) val entries: List<Entry>
)