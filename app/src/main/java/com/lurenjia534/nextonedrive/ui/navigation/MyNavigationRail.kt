package com.lurenjia534.nextonedrive.ui.navigation
// ui/navigation/MyNavigationRail.kt

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lurenjia534.nextonedrive.MediaPreview.AudioPreviewScreen
import com.lurenjia534.nextonedrive.MediaPreview.ImagePreviewScreen
import com.lurenjia534.nextonedrive.MediaPreview.VideoPreviewScreen
import com.lurenjia534.nextonedrive.ui.screens.favorites.FavoritesScreen
import com.lurenjia534.nextonedrive.ui.screens.inbox.InboxScreen
import com.lurenjia534.nextonedrive.ui.screens.outbox.OutboxScreen
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MyNavigationRail(innerPadding: PaddingValues = PaddingValues(0.dp)) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // 定义导航栏物品列表及其对应的路线
    val items = listOf("inbox", "outbox", "favorites", "trash", "label/1", "label/2")
    val selectedItem = remember { mutableStateOf(items[0]) }

    // 使用 Row 以便左侧放置 NavigationRail，右侧放置内容
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            header = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                )
            },
            content = {
                // 遍历物品列表以创建 NavigationRailItem
                items.forEach { item ->
                    NavigationRailItem(
                        selected = item == selectedItem.value,
                        onClick = {
                            selectedItem.value = item
                            scope.launch {
                                navController.navigate(item) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = {
                            Text(item.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                            })
                        },
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    "inbox" -> Icons.Outlined.Home
                                    "outbox" -> Icons.AutoMirrored.Outlined.Send
                                    "favorites" -> Icons.Outlined.FavoriteBorder
                                    "trash" -> Icons.Outlined.Delete
                                    else -> Icons.Outlined.ArrowDropDown
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        )

        // 内容区域
        NavHost(
            navController = navController,
            startDestination = "inbox",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("inbox") { InboxScreen(navController) }
            composable("outbox") { OutboxScreen() }
            composable("favorites") { FavoritesScreen(navController) }
            composable("trash") { TrashScreen() }
            composable(
                route = "label/{labelId}",
                arguments = listOf(navArgument("labelId") { type = NavType.StringType })
            ) { backStackEntry ->
                val labelId = backStackEntry.arguments?.getString("labelId") ?: "Unknown"
                LabelScreen(label = labelId)
            }
            composable(
                route = "image_preview/{imageUrl}",
                arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                ImagePreviewScreen(imageUrl = imageUrl, navController = navController)
            }
            composable(
                route = "video_preview/{videoUrl}",
                arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
                VideoPreviewScreen(videoUrl = videoUrl, navController = navController)
            }
            composable(
                route = "audio_preview/{audioUrl}",
                arguments = listOf(navArgument("audioUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val audioUrl = backStackEntry.arguments?.getString("audioUrl") ?: ""
                AudioPreviewScreen(audioUrl = audioUrl, navController = navController)
            }
        }
    }
}

