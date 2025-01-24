package com.niki.vocabulary

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.ui.composables.CsvImport
import com.niki.vocabulary.ui.composables.HomeScreen
import com.niki.vocabulary.ui.theme.VocabularyTheme
import kotlinx.coroutines.launch

enum class Screen {
    HOME, CSV_IMPORT,
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screen.HOME.name)
    data object CsvImport : NavigationItem(Screen.CSV_IMPORT.name)
}

@Composable
fun Database(context: Context, onInit: (db: AppDatabase) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    var db by remember { mutableStateOf<AppDatabase?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            db = Room.databaseBuilder(
                context, AppDatabase::class.java, "vocabulary"
            ).build()

            onInit(db!!)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            var db by remember { mutableStateOf<AppDatabase?>(null) }
            Database(applicationContext, onInit = { db = it })

            VocabularyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavigationItem.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavigationItem.Home.route) {
                            HomeScreen(db, navController)
                        }
                        composable(NavigationItem.CsvImport.route) {
                            db?.let {
                                CsvImport(db = it,
                                    onFinish = { navController.navigate(NavigationItem.Home.route) })
                            }
                        }
                    }
                }
            }
        }
    }
}