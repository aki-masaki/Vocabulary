package com.niki.vocabulary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.dao.EntryDao
import com.niki.vocabulary.data.entity.Entry
import com.niki.vocabulary.ui.theme.VocabularyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val coroutineScope = rememberCoroutineScope()

            var db: AppDatabase?
            var entryDao by remember { mutableStateOf<EntryDao?>(null) }
            var entry by remember { mutableStateOf<Entry?>(null) }
            var count by remember { mutableIntStateOf(0) }

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    db = Room.databaseBuilder(
                        applicationContext, AppDatabase::class.java, "vocabulary"
                    ).build()

                    entryDao = db?.entryDao()

                    entryDao?.insert(Entry("word", "definition"))

                    count = entryDao?.getCount() ?: 0
                    entry = entryDao?.getFirst()
                }
            }

            VocabularyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(modifier = Modifier.padding(innerPadding)) {
                        Text(text = entry?.word ?: "Word")
                        Text(text = entry?.definition ?: "Definition")
                    }
                }
            }
        }
    }
}