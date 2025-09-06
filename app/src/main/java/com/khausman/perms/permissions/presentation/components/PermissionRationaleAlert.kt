package com.khausman.perms.permissions.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PermissionRationaleAlert(
    title: String,
    description: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        modifier = Modifier,
        icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning") },
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = description) },
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                (Text(text = confirmButtonText))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                (Text(text = dismissButtonText))
            }
        }
    )
}