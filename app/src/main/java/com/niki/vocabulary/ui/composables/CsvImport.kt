package com.niki.vocabulary.ui.composables

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Entry
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun CsvImport(modifier: Modifier = Modifier, db: AppDatabase, onFinish: () -> Unit) {
    val contentResolver = LocalContext.current.contentResolver

    val coroutineScope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            val inputStream = contentResolver.openInputStream(uri)

            val reader = BufferedReader(InputStreamReader(inputStream))
            var line = reader.readLine()

            while (line != null && line.isNotBlank()) {
                // Find the index of the first '"' character
                var pos = line.indexOf("\"")

                if (pos == -1) continue

                // Find the index of the second '"' character
                pos = line.indexOf("\"", startIndex = pos + 1);

                if (pos == -1) continue

                // Substring ignoring the '"' characters
                val word = line.substring(1, pos)
                val definition = line.substring(pos + 3, line.length - 1)

                coroutineScope.launch {
                    db.entryDao().insert(Entry(word, definition))
                }

                line = reader.readLine()
            }

            onFinish()
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(PaddingValues(top = 50.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Import CSV data", fontSize = 30.sp)

        Button(onClick = {
            launcher.launch(
                arrayOf("text/csv")
            )
        }) {
            Text(text = "Select file")
        }
    }
}