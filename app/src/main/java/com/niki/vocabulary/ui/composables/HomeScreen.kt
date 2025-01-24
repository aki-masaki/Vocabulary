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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.niki.vocabulary.data.entity.Entry
import com.niki.vocabulary.data.entity.Like
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
        val likeDao = db.likeDao()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                count = entryDao.getCount()
                entryDao.getRandomList().let { entryList = it.toMutableStateList() }

                if (entryId != -1) entryList = listOf(
                    listOf(entryDao.getFromId(entryId)),
                    entryList.slice(IntRange(start = 1, endInclusive = entryList.size - 1))
                ).flatten()
            }
        }

        if (count == 0) navController.navigate(NavigationItem.CsvImport.route)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

                var like by remember { mutableStateOf<Like?>(null) }

                IconButton(
                    onClick = {
                        context.startActivity(shareIntent)
                    },
                    modifier = buttonModifier,
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = "Share")
                }

                IconButton(
                    onClick = {}, modifier = buttonModifier
                ) {
                    Icon(Icons.Rounded.Bookmark, contentDescription = "Bookmark")
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (like != null) {
                                likeDao.delete(like!!)

                                like = null
                            } else {
                                like = Like(entryList[pagerState.currentPage].id)

                                likeDao.insert(like!!)
                            }
                        }
                    }, modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            BorderStroke(
                                3.dp,
                                if (like == null) MaterialTheme.colorScheme.surfaceVariant else Color(
                                    0xFFF74636
                                )
                            ), shape = RoundedCornerShape(20.dp)
                        )
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    LaunchedEffect(pagerState.currentPage) {
                        coroutineScope.launch(Dispatchers.IO) {
                            if (entryList.isNotEmpty()) like =
                                likeDao.getByEntryId(entryList[pagerState.currentPage].id)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = "Like",
                            tint = if (like == null) MaterialTheme.colorScheme.onSurface else Color(
                                0xFFF74636
                            ),
                            modifier = Modifier.size(if (like == null) 23.dp else 30.dp)
                        )
                    }
                }
            }
        }
    }
}