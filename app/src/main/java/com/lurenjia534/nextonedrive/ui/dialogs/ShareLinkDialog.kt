package com.lurenjia534.nextonedrive.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareLinkDialog(
    onDismiss: () -> Unit,
    onShareLinkCreated: (linkType:String, scope:String?) -> Unit
) {
    var selectedLinkType by remember { mutableStateOf("view") }
    var selectedScope by remember { mutableStateOf("anonymous") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Share Link") },
        text = {
            Column {
                Text("Select Link Type:")

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedLinkType == "view",
                        onClick = { selectedLinkType = "view" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View (Read-only)")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedLinkType == "edit",
                        onClick = { selectedLinkType = "edit" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit (Editable)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Scope:")

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedScope == "anonymous",
                        onClick = { selectedScope = "anonymous" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Anonymous (Anyone with the link)")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedScope == "organization",
                        onClick = { selectedScope = "organization" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Organization (Logged-in users only)")
                }
            }
        },
        confirmButton = {
            TextButton (
                onClick = {
                    onShareLinkCreated(selectedLinkType, selectedScope)
                    onDismiss() // 关闭对话框
                }
            ) {
                Text("Create Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}