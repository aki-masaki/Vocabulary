package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Entry
import com.niki.vocabulary.data.entity.RecentSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(database: AppDatabase?, onSelect: (entryId: Int) -> Unit) {
    var searchInput by remember { mutableStateOf("") }

    var results by remember { mutableStateOf<List<Entry>>(listOf()) }
    val coroutineScope = rememberCoroutineScope()

    var recentSearches by remember { mutableStateOf<List<RecentSearch>>(listOf()) }

    var fieldEmpty by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    database?.let { db ->
        val entryDao = db.entryDao()
        val recentSearchDao = db.recentSearchDao()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()

            coroutineScope.launch {
                recentSearches = recentSearchDao.getAll()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp))
        ) {
            TextField(value = searchInput,
                onValueChange = {
                    searchInput = it

                    if (it.isNotBlank()) {
                        coroutineScope.launch(Dispatchers.IO) {
                            results = entryDao.getFromWord("$it%")
                        }

                        fieldEmpty = false
                    } else if (it.isBlank()) fieldEmpty = true
                },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                placeholder = { Text(text = "Search here") })

            if (!fieldEmpty) Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(
                        PaddingValues(top = 20.dp, bottom = 20.dp)
                    )
                    .verticalScroll(rememberScrollState()),
            ) {
                for (entry in results) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clickable {
                                onSelect(entry.id)

                                coroutineScope.launch(Dispatchers.IO) {
                                    if (recentSearches.find { it.entryId == entry.id } == null) recentSearchDao.insert(
                                        RecentSearch(entry.id)
                                    )
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    PaddingValues(
                                        start = 10.dp, end = 10.dp, top = 10.dp, bottom = 10.dp
                                    )
                                ), verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = entry.word)

                            Text(text = entry.definition, fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            }
            else if (recentSearches.isEmpty()) Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(100.dp)
                )
                Text(text = "No recent searches", fontSize = 25.sp)
            }
            else Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(
                        PaddingValues(top = 20.dp, bottom = 20.dp)
                    )
                    .verticalScroll(rememberScrollState()),
            ) {
                var entry by remember { mutableStateOf<Entry?>(null) }

                for (search in recentSearches) {
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            entry = entryDao.getFromId(search.entryId)
                        }
                    }

                    entry?.let {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .clickable { onSelect(search.entryId) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    PaddingValues(end = 5.dp)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1F)
                                        .padding(
                                            PaddingValues(
                                                start = 10.dp,
                                                end = 10.dp,
                                                top = 10.dp,
                                                bottom = 10.dp
                                            )
                                        ), verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = it.word)

                                    Text(
                                        text = it.definition, fontSize = 13.sp, color = Color.Gray
                                    )
                                }

                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        recentSearchDao.delete(
                                            search
                                        )

                                        recentSearches = recentSearchDao.getAll()
                                    }
                                }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            recentSearchDao.deleteAll()

                            recentSearches = listOf()
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(23.dp),
                        )
                        .width(140.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "X")
                        Text(text = "Clear all", fontSize = 16.sp)
                    }
                }
            }


        }
    }
}