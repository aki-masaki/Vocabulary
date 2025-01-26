package com.niki.vocabulary.ui.composables

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.niki.vocabulary.NavigationItem
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Collection
import com.niki.vocabulary.data.entity.Entry
import com.niki.vocabulary.data.entity.relations.CollectionEntryCrossRef
import com.niki.vocabulary.data.entity.relations.CollectionWithEntries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(database: AppDatabase?, navController: NavController, entryId: Int = -1) {
    val coroutineScope = rememberCoroutineScope()

    var count by remember { mutableIntStateOf(-1) }
    var entryList by remember { mutableStateOf<List<Entry>>(listOf()) }

    val pagerState = rememberPagerState(pageCount = { entryList.size })

    var sendIndent by remember { mutableStateOf<Intent?>(null) }
    var shareIntent by remember { mutableStateOf<Intent?>(null) }
    val context = LocalContext.current

    if (entryList.isNotEmpty()) {
        sendIndent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "${entryList[pagerState.currentPage].word} - ${entryList[pagerState.currentPage].definition}"
            )
            type = "text/plain"
        }

        shareIntent = Intent.createChooser(sendIndent, null)
    }

    database?.let { db ->
        val entryDao = db.entryDao()
        val collectionDao = db.collectionDao()

        var collections by remember { mutableStateOf<List<Collection>>(listOf()) }
        var collectionsIncluding by remember { mutableStateOf<List<CollectionWithEntries>>(listOf()) }
        var collectionsOpen by remember { mutableStateOf(false) }

        LaunchedEffect(pagerState.currentPage) {
            coroutineScope.launch(Dispatchers.IO) {
                if (entryList.isNotEmpty()) {
                    collections = collectionDao.getAll()
                    collectionsIncluding =
                        collectionDao.getByEntryId(entryList[pagerState.currentPage].id)
                }

                println(collectionsIncluding)
            }
        }

        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.IO) {
                count = entryDao.getCount()
                entryDao.getRandomList().let { entryList = it.toMutableStateList() }

                if (entryId != -1) entryList = listOf(
                    listOf(entryDao.getFromId(entryId)),
                    entryList.slice(IntRange(start = 1, endInclusive = entryList.size - 1))
                ).flatten()

                if (entryList.isNotEmpty()) {
                    collections = collectionDao.getAll()
                    collectionsIncluding =
                        collectionDao.getByEntryId(entryList[pagerState.currentPage].id)
                }

                println(collectionsIncluding)
            }
        }

        if (count == 0) navController.navigate(NavigationItem.CsvImport.route)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (collectionsOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(8F)
                        .verticalScroll(rememberScrollState())
                        .padding(PaddingValues(horizontal = 20.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        20.dp, alignment = Alignment.CenterVertically
                    )
                ) {
                    for (collection in collections) {
                        if (collection.id == 1) continue

                        val collectionIncluding =
                            collectionsIncluding.find { it.collection.id == collection.id }

                        var isIncluded by remember { mutableStateOf(collectionIncluding != null) }
                        var crossRef by remember { mutableStateOf<CollectionEntryCrossRef?>(null) }

                        Button(onClick = {
                            coroutineScope.launch {
                                if (crossRef == null) crossRef = CollectionEntryCrossRef(
                                    entryId = entryList[pagerState.currentPage].id,
                                    collectionId = collection.id
                                )

                                if (isIncluded) {
                                    collectionDao.deleteCollectionEntryCrossRef(crossRef!!)
                                } else {
                                    collectionDao.insertCollectionEntryCrossRef(crossRef!!)
                                }

                                collectionsIncluding =
                                    collectionDao.getByEntryId(entryList[pagerState.currentPage].id)

                                isIncluded = !isIncluded
                            }
                        }, primary = isIncluded, content = {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                iconMap[collection.iconName]?.let {
                                    Icon(
                                        it, contentDescription = collection.iconName
                                    )
                                }

                                Text(text = collection.name)
                            }
                        })
                    }
                }
            } else {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(8F),
                ) { entry ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 20.dp, alignment = Alignment.CenterVertically
                        )
                    ) {
                        Text(text = entryList[entry].word, fontSize = 30.sp)
                        Text(
                            text = entryList[entry].definition, modifier = Modifier.padding(
                                PaddingValues(start = 30.dp, end = 30.dp)
                            ), textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .weight(1F)
                    .padding(PaddingValues(start = 50.dp, end = 50.dp, bottom = 20.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val buttonModifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)

                var isSaved by remember { mutableStateOf(collectionsIncluding.isNotEmpty() && collectionsIncluding.find { it.collection.id == 1 } == null) }

                var crossRef by remember { mutableStateOf<CollectionEntryCrossRef?>(null) }
                var isLiked by remember { mutableStateOf(collectionsIncluding.find { it.collection.id == 1 } != null) }

                LaunchedEffect(collectionsIncluding) {
                    isLiked = collectionsIncluding.find { it.collection.id == 1 } != null

                    isSaved =
                        if (collectionsIncluding.size == 1) !isLiked else collectionsIncluding.size > 1
                }

                IconButton(
                    onClick = {
                        context.startActivity(shareIntent)
                    },
                    modifier = buttonModifier,
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = "Share")
                }

                IconButton(
                    onClick = {
                        collectionsOpen = !collectionsOpen

                        coroutineScope.launch {
                            collectionsIncluding =
                                collectionDao.getByEntryId(entryList[pagerState.currentPage].id)
                        }
                    }, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            BorderStroke(
                                3.dp, if (isSaved) Color(
                                    0xFFD6D11F
                                ) else MaterialTheme.colorScheme.surfaceVariant
                            ), shape = RoundedCornerShape(20.dp)
                        )
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Icon(
                        Icons.Rounded.Bookmark,
                        contentDescription = "Bookmark",
                        tint = if (isSaved) Color(
                            0xFFD6D11F
                        ) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(if (isSaved) 30.dp else 23.dp)
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (collectionsIncluding.find { it.collection.id == 1 } != null) {
                                crossRef = collectionDao.getCollectionEntryCrossRef(
                                    entryList[pagerState.currentPage].id, 1
                                )

                                collectionDao.deleteCollectionEntryCrossRef(
                                    crossRef!!
                                )
                            } else {
                                if (crossRef == null) crossRef = CollectionEntryCrossRef(
                                    entryId = entryList[pagerState.currentPage].id, collectionId = 1
                                )

                                collectionDao.insertCollectionEntryCrossRef(crossRef!!)
                            }

                            collectionsIncluding =
                                collectionDao.getByEntryId(entryList[pagerState.currentPage].id)

                            isLiked = !isLiked
                        }
                    }, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            BorderStroke(
                                3.dp, if (isLiked) Color(
                                    0xFFF74636
                                ) else MaterialTheme.colorScheme.surfaceVariant
                            ), shape = RoundedCornerShape(20.dp)
                        )
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = "Like",
                            tint = if (isLiked) Color(
                                0xFFF74636
                            ) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(if (isLiked) 30.dp else 23.dp)
                        )
                    }
                }
            }
        }
    }
}