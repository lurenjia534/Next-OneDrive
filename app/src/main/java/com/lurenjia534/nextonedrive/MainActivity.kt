package com.lurenjia534.nextonedrive

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.lurenjia534.nextonedrive.ui.theme.NextOneDriveTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lurenjia534.nextonedrive.OAuthToken.fetchAccessToken


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextOneDriveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyNavigationDrawer(innerPadding)
                }
            }
        }
    }
}

@Preview
@Composable
fun MyNavigationDrawer(
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Mail",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                NavigationDrawerItem(
                    label = { Text("Inbox") },
                    selected = true,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("inbox")
                        }
                    },
                    icon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Outbox") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("outbox")
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("favorites")
                        }
                    },
                    icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Trash") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("trash")
                        }
                    },
                    icon = { Icon(Icons.Outlined.Delete, contentDescription = null) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Labels",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                NavigationDrawerItem(
                    label = { Text("Label 1") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("label/1")
                        }
                    },
                    icon = { Icon(Icons.Outlined.ArrowDropDown, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Label 2") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate("label/2")
                        }
                    },
                    icon = { Icon(Icons.Outlined.ArrowDropDown, contentDescription = null) }
                )
            }
        },
        content = {NavHost(
            navController = navController,
            startDestination = "inbox"
        ) {
            composable("inbox") { InboxScreen() }
            composable("outbox") { OutboxScreen() }
            composable("favorites") { FavoritesScreen() }
            composable("trash") { TrashScreen() }
            composable(
                route = "label/{labelId}",
                arguments = listOf(navArgument("labelId") { type = NavType.StringType })
            ){ backStackEntry ->
                val labelId = backStackEntry.arguments?.getString("labelId") ?: "Unknown"
                LabelScreen(label = labelId)
            }
            }
        }
    )
}

@Composable
fun InboxScreen() {
    // State variables for input fields
    var tenantId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var grantType by remember { mutableStateOf("client_credentials") }
    var scope by remember { mutableStateOf("https://graph.microsoft.com/.default offline_access") }
    // ErrorSnackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    // onSurface Snackbar Status
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    // Main layout
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor =  if (showSuccessSnackbar) Color.Black else Color.Black,  // 判断是否为成功 Snackbar 来设置背景颜色
                    shape = MaterialTheme.shapes.extraSmall
                )
            }
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Next OneDrive LOGIN",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            // Tenant ID TextField
            OutlinedTextField(
                value = tenantId,
                onValueChange = { tenantId = it },
                label = { Text("Tenant ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Client ID TextField
            OutlinedTextField(
                value = clientId,
                onValueChange = { clientId = it },
                label = { Text("Client ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Client Secret TextField
            OutlinedTextField(
                value = clientSecret,
                onValueChange = { clientSecret = it },
                label = { Text("Client Secret") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // User ID TextField
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Grant Type TextField
            OutlinedTextField(
                value = grantType,
                onValueChange = { grantType = it },
                label = { Text("Grant Type") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
            // Scope TextField
            OutlinedTextField(
                value = scope,
                onValueChange = { scope = it },
                label = { Text("Scope") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
            val context = LocalContext.current
            // Login Button
            OutlinedButton(
                onClick = {
                    // 这里你可以保存输入内容，或者将它们用于后续的 OAuth2 请求
                    fetchAccessToken(
                        context = context,
                        tenantId = tenantId,
                        clientId = clientId,
                        clientSecret = clientSecret,
                        grantType = grantType,
                        scope = scope,
                        userId = userId,
                        onError = { message ->
                            errorMessage = message
                            showErrorSnackbar = true // 重置状态
                        },
                        onSuccess = { token ->
                            // 成功后的操作
                            successMessage = "Token: $token"
                            println("Token $token")
                            showSuccessSnackbar = true // 重置状态
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Login", color = Color.White)
            }
        }
        // Show error snackbar
        if (showErrorSnackbar) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(errorMessage)
                showErrorSnackbar = false // 重置状态
            }
        }
        // Show success snackbar
        if (showSuccessSnackbar){
            LaunchedEffect(snackbarHostState){
                snackbarHostState.showSnackbar(successMessage)
                showSuccessSnackbar = false // 重置状态
                print("Token: $successMessage")
            }
        }
    }
}

@Composable
fun OutboxScreen() {
    Text("Outbox Screen", modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
}

@Composable
fun FavoritesScreen() {
    Text("Favorites Screen", modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
}

@Composable
fun TrashScreen() {
    Text("Trash Screen", modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
}

@Composable
fun LabelScreen(label: String) {
    Text("$label Screen", modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
}
