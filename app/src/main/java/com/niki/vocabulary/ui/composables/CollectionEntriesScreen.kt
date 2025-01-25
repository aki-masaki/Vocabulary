package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.relations.CollectionWithEntries
import kotlinx.coroutines.launch

@Composable
fun CollectionEntriesScreen(
    database: AppDatabase?, collectionId: Int, onSelect: (entryId: Int) -> Unit
) {
    if (collectionId == -1) return

    val coroutineScope = rememberCoroutineScope()

    var collectionWithEntries by remember { mutableStateOf<CollectionWithEntries?>(null) }

    database?.let { db ->
        val collectionDao = db.collectionDao()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                collectionWithEntries = collectionDao.getById(collectionId)
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            collectionWithEntries?.let {
                Preview(
                    collection = it.collection, db, collectionExists = true
                )
            }

            Button(onClick = {
                coroutineScope.launch {
                    collectionWithEntries?.collection?.id?.let { collectionId ->
                        collectionDao.getCrossRefByCollectionId(
                            collectionId
                        ).forEach { collectionDao.deleteCollectionEntryCrossRef(it) }
                    }

                    collectionWithEntries = collectionDao.getById(collectionId)
                }
            }, primary = false, content = {
                Text(text = "Clear all")
            })

            collectionWithEntries?.entries?.forEachIndexed { index, entry ->
                EntryCard(onClick = { onSelect(entry.id) }, entry = entry, onIconClick = {
                    coroutineScope.launch {
                        collectionDao.getCollectionEntryCrossRef(entry.id, collectionId)?.let {
                            collectionDao.deleteCollectionEntryCrossRef(
                                it
                            )
                        }

                        collectionWithEntries = collectionDao.getById(collectionId)
                    }
                })
            }

            if (collectionWithEntries?.entries?.isEmpty() == true) {
                Text(text = "No items", fontSize = 25.sp)
            }
        }
    }
}