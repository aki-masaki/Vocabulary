package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(database: AppDatabase?, onSelect: (entryId: Int) -> Unit) {
    var searchInput by remember { mutableStateOf("") }

    var results by remember { mutableStateOf<List<Entry>>(listOf()) }
    val coroutineScope = rememberCoroutineScope()

    database?.let { db ->
        val entryDao = db.entryDao()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp))
        ) {
            TextField(value = searchInput,
                onValueChange = {
                    searchInput = it

                    if (it != "") coroutineScope.launch(Dispatchers.IO) {
                        results = entryDao.getFromWord("$it%")
                    }
                },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                placeholder = { Text(text = "Search here") })

            Column(
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
                            .clickable { onSelect(entry.id) },
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
        }
    }
}