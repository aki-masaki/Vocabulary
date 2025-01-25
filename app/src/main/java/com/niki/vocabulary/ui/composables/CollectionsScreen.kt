package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Collection
import com.niki.vocabulary.data.entity.relations.CollectionEntryCrossRef
import kotlinx.coroutines.launch

@Composable
fun CollectionCard(collection: Collection, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(30.dp))
            .border(
                BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(30.dp)
            )
            .width(100.dp)
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                20.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Icon(
                Icons.Rounded.Bookmark,
                contentDescription = "Icon",
                modifier = Modifier.size(50.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = collection.name, fontSize = 18.sp)
                Text(text = "5 words", color = Color.Gray)
            }
        }
    }
}

@Composable
fun AddCollectionCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(30.dp))
            .border(
                BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(30.dp)
            )
            .width(100.dp)
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                20.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Icon", modifier = Modifier.size(50.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Add", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CollectionsScreen(database: AppDatabase?) {
    val coroutineScope = rememberCoroutineScope()

    var collections by remember { mutableStateOf<List<Collection>>(listOf()) }

    database?.let { db ->
        val entryDao = db.entryDao()
        val collectionDao = db.collectionDao()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val likedCollection = Collection(name = "Liked")
                val randomCollection = Collection(name = "Random")
                val usefulCollection = Collection(name = "Useful")

                val entry = entryDao.getFirst()

                collections = collectionDao.getAll()

                if (collections.isEmpty()) {
                    val collectionId = collectionDao.insert(likedCollection)
                    collectionDao.insert(randomCollection)
                    collectionDao.insert(usefulCollection)

                    collectionDao.insertCollectionEntryCrossRef(
                        CollectionEntryCrossRef(
                            entry.id, collectionId.toInt()
                        )
                    )
                }

                collections = collectionDao.getAll()
            }
        }

        Column(
            modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            collections.forEachIndexed { index, _ ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        30.dp
                    )
                ) {
                    if (index < collections.size - 2) {
                        CollectionCard(
                            collection = collections[index * 2], modifier = Modifier.weight(.5F)
                        )

                        CollectionCard(
                            collection = collections[index + 1], modifier = Modifier.weight(.5F)
                        )
                    } else if (index == collections.size - 2) {
                        CollectionCard(
                            collection = collections[collections.size - 1],
                            modifier = Modifier.weight(.5F)
                        )

                        AddCollectionCard(modifier = Modifier.weight(.5F))
                    }
                }
            }
        }
    }
}