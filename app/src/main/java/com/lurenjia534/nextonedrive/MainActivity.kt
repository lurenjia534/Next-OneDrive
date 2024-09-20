// MainActivity.kt
package com.lurenjia534.nextonedrive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lurenjia534.nextonedrive.ui.navigation.NavigationGraph
import com.lurenjia534.nextonedrive.ui.theme.NextOneDriveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextOneDriveTheme {
                NavigationGraph() // 从 NavGraph.kt 中导入
            }
        }
    }
}
