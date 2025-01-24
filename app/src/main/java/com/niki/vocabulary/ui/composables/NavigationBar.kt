package com.niki.vocabulary.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.niki.vocabulary.Screen

data class NavigationBarItem(
    val route: String = "", val label: String = "", val icon: ImageVector? = null
) {
    fun items(): Array<NavigationBarItem> {
        return arrayOf(
            NavigationBarItem(
                route = Screen.Home.name, label = "Home", icon = Icons.Rounded.Home
            ), NavigationBarItem(
                route = Screen.Search.name, label = "Search", icon = Icons.Rounded.Search
            ), NavigationBarItem(
                route = Screen.Practice.name, label = "Practice", icon = Icons.Rounded.School
            ), NavigationBarItem(
                route = Screen.Settings.name, label = "Settings", icon = Icons.Rounded.Settings
            )
        )
    }
}

@Composable
fun NavigationBar(onNavigate: (route: String) -> Unit) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(PaddingValues(start = 20.dp, end = 20.dp, bottom = 30.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavigationBarItem().items().forEachIndexed { index, item ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = interactionSource, indication = null
                            ) {
                                selectedItem = index
                                onNavigate(item.route)
                            }) {
                        Icon(
                            item.icon!!,
                            contentDescription = item.label,
                            Modifier.size(30.dp),
                            tint = if (index == selectedItem) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }
    }
}