package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Abc
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.niki.vocabulary.Screen
import com.niki.vocabulary.data.AppDatabase
import com.niki.vocabulary.data.entity.Collection
import kotlinx.coroutines.launch

val iconMap: Map<String, ImageVector> = mapOf(
    "Favorite" to Icons.Rounded.Favorite,
    "Star" to Icons.Rounded.Star,
    "School" to Icons.Rounded.School,
    "Bookmark" to Icons.Rounded.Bookmark,
    "Search" to Icons.Rounded.Search,
    "Book" to Icons.Rounded.Book,
    "Share" to Icons.Rounded.Share,
    "Home" to Icons.Rounded.Home
)

val colorList: List<Long> = listOf(
    0xFF2379AF, 0xFF23AF3C, 0xFFD13C1F, 0xFFD16F1F, 0xFFD6D11F, 0xFF5FDD5A, 0xFFD75ADD, 0xFF3C3FE8
)

@Composable
fun IconList(onSelect: (index: Int) -> Unit, selectedIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 10.dp))
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        iconMap.entries.forEachIndexed { index, entry ->
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        if (selectedIndex == index) BorderStroke(
                            3.dp, MaterialTheme.colorScheme.primary
                        ) else BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(indication = null, interactionSource = interactionSource) {
                        onSelect(
                            index
                        )
                    },

                ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        entry.value, contentDescription = entry.key, modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ColorList(onSelect: (index: Int) -> Unit, selectedIndex: Int) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 10.dp))
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colorList.forEachIndexed { index, colorLong ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        if (selectedIndex == index) BorderStroke(
                            3.dp, MaterialTheme.colorScheme.primary
                        ) else BorderStroke(3.dp, MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(indication = null, interactionSource = interactionSource) {
                        onSelect(
                            index
                        )
                    },

                ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .background(Color(colorLong), shape = RoundedCornerShape(10.dp))
                    ) {

                    }
                }
            }
        }
    }
}

@Composable
fun Preview(collection: Collection, db: AppDatabase, collectionExists: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
        )

        CollectionCard(collection,
            modifier = Modifier.weight(.5F),
            db,
            preview = !collectionExists,
            onClick = {})

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
        )
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(text = title, fontSize = 25.sp, modifier = Modifier.offset(15.dp))

        content()
    }
}

@Composable
fun CreateCollectionScreen(database: AppDatabase?, navController: NavController) {
    var selectedIconIndex by remember { mutableIntStateOf(0) }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("New Collection") }

    val coroutineScope = rememberCoroutineScope()

    database?.let { db ->
        Column(modifier = Modifier.fillMaxWidth()) {
            Preview(
                Collection(
                    name = name,
                    iconName = iconMap.entries.toList()[selectedIconIndex].key,
                    colorLong = colorList[selectedColorIndex]
                ), db
            )

            Section(title = "Icon") {
                IconList(onSelect = { selectedIconIndex = it }, selectedIndex = selectedIconIndex)
            }

            Section(title = "Color") {
                ColorList(
                    onSelect = { selectedColorIndex = it }, selectedIndex = selectedColorIndex
                )
            }

            Section(title = "Name") {
                Input(
                    value = name,
                    onValueChange = { name = it },
                    icon = Icons.Rounded.Abc,
                    placeholder = "New Collection"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(horizontal = 10.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(onClick = { navController.popBackStack() }, content = {
                    Text(text = "Cancel")
                }, modifier = Modifier.weight(.5F))

                Button(onClick = {
                    coroutineScope.launch {
                        db.collectionDao().insert(
                            Collection(
                                name,
                                iconName = iconMap.entries.toList()[selectedIconIndex].key,
                                colorLong = colorList[selectedColorIndex]
                            )
                        )

                        navController.navigate(Screen.Practice.name)
                    }
                }, content = {
                    Text(text = "Confirm")
                }, modifier = Modifier.weight(.5F))
            }
        }
    }
}