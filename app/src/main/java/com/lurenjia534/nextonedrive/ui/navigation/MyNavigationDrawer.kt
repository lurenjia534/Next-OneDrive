package com.lurenjia534.nextonedrive.ui.navigation

// ui/navigation/MyNavigationDrawer.kt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun MyNavigationDrawer(innerPadding: PaddingValues = PaddingValues(0.dp)) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // 定义抽屉物品列表及其对应的路线
    val items = listOf("inbox", "outbox", "favorites", "trash", "label/1", "label/2")
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Next OneDrive",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                // Loop through items to create the NavigationDrawerItem for each
                items.forEach { item ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        ),
                        label = {
                            Text(item.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            })
                        },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            selectedItem.value = item
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = "inbox"
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
    )
}

@Composable
fun LabelScreen(label: String) {

}

@Composable
fun TrashScreen() {
   Box(
         modifier = Modifier.fillMaxSize()
    ) {
         Text(
              text = "Trash Screen",
              modifier = Modifier.padding(16.dp)
         )
    }
}
