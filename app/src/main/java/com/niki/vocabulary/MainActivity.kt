package com.niki.vocabulary

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.ui.composables.CollectionsScreen
import com.niki.vocabulary.ui.composables.CsvImport
import com.niki.vocabulary.ui.composables.HomeScreen
import com.niki.vocabulary.ui.composables.NavigationBar
import com.niki.vocabulary.ui.composables.SearchScreen
import com.niki.vocabulary.ui.theme.VocabularyTheme
import kotlinx.coroutines.launch

enum class Screen {
    Home, CsvImport, Search, Practice, Settings
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screen.Home.name)
    data object CsvImport : NavigationItem(Screen.CsvImport.name)
    data object Search : NavigationItem(Screen.Search.name)
    data object Practice : NavigationItem(Screen.Practice.name)
    data object Settings : NavigationItem(Screen.Settings.name)
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
            var selectedItem by remember { mutableIntStateOf(0) }

            Database(applicationContext, onInit = { db = it })

            VocabularyTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    NavigationBar(onNavigate = { route, index ->
                        navController.navigate(route)

                        selectedItem = index
                    }, selectedItem)
                }) { innerPadding ->
                    NavHost(
                        navController = navController,
                        //startDestination = "${NavigationItem.Home.route}/-1",
                        startDestination = NavigationItem.Practice.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            "${NavigationItem.Home.route}/{entryId}",
                            arguments = listOf(navArgument("entryId") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            HomeScreen(
                                db, navController, backStackEntry.arguments?.getInt("entryId") ?: -1
                            )
                        }
                        composable(NavigationItem.Search.route) {
                            SearchScreen(db,
                                onSelect = { entryId ->
                                    navController.navigate("${Screen.Home.name}/$entryId")

                                    selectedItem = 0
                                })
                        }
                        composable(NavigationItem.Practice.route) {
                            CollectionsScreen(db)
                        }
                        composable(NavigationItem.Settings.route) {
                            Text(text = "Settings")
                        }
                        composable(NavigationItem.CsvImport.route) {
                            db?.let {
                                CsvImport(db = it,
                                    onFinish = { navController.navigate("${NavigationItem.Home.route}/-1") })
                            }
                        }
                    }
                }
            }
        }
    }
}