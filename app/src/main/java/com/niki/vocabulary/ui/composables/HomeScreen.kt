package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.niki.vocabulary.NavigationItem
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Entry
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(database: AppDatabase?, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var count by remember { mutableIntStateOf(-1) }
    var entryList by remember { mutableStateOf<List<Entry>>(listOf()) }

    val pagerState = rememberPagerState(pageCount = { entryList.size })

    database?.let { db ->
        val entryDao = db.entryDao()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                count = entryDao.getCount()
                entryDao.getRandomList().let { entryList = it }
            }
        }

        if (count == 0) navController.navigate(NavigationItem.CsvImport.route)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { entry ->
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
    }
}