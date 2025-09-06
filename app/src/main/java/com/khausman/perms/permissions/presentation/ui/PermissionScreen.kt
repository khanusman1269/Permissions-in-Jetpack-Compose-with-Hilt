package com.khausman.perms.permissions.presentation.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.khausman.perms.permissions.presentation.components.PermissionRationaleAlert
import com.khausman.perms.permissions.presentation.viewmodel.PermissionViewModel

@Composable
fun PermissionScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val permissionViewModel = hiltViewModel<PermissionViewModel>()
    val buttonText = remember { mutableStateOf("") }

    val state by permissionViewModel.state.collectAsStateWithLifecycle()
    val showAlert = remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            permissionViewModel.onResult(activity = activity, result = it)
        }
    )

//    ask for permissions
    LaunchedEffect(state.requestPermissions) {
        if (state.requestPermissions) {
            permissionLauncher.launch(state.permissions.toTypedArray())
        }
    }

//    if should show app rationale, show alert
    LaunchedEffect(state.showRationale) {
        if (state.showRationale) {
            showAlert.value = true
            permissionViewModel.onPermissionRequested()
        }
    }

//    when state changes & navigateToNextScreen is true, move to relevant screen
    LaunchedEffect(state.navigateToNextScreen) {
        if (state.navigateToNextScreen) {
            buttonText.value = "Next"
        }
    }

//    when state changes & navigateToSettings is true, open settings
    LaunchedEffect(state.navigateToSettings) {
        if (state.navigateToSettings) {
            showAlert.value = true
        }
    }

//    on activity result check if permission granted
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            permissionViewModel.checkPermissionsAfterSettings(context)
        }
    )

    PermissionView(buttonText = buttonText.value, onAllow = permissionViewModel::onGrantPermissionClick)

    if (showAlert.value) {
        val description = buildPermissionDescription(state.rationals)
        val confirmButtonText = if (state.navigateToSettings) "Settings" else "Allow"
        val dismissButtonText = if (state.navigateToSettings) "Finish" else "Cancel"
        PermissionRationaleAlert(
            title = "Permission Required",
            description = description,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            onConfirmation = {
                showAlert.value = false
                if (state.navigateToSettings) {
//                    open settings if user denied permanently else request again
                    settingsLauncher.launch(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                    )
                } else {
                    permissionLauncher.launch(state.permissions.toTypedArray())
                }
            },
            onDismissRequest = {
                showAlert.value = false
                if (state.navigateToSettings) {
                    permissionViewModel.updateSettingsNavigation()
                }
            },
            onCancel = {
                showAlert.value = false
                if (state.navigateToSettings) {
                    activity.finish()
                } else {
                    permissionViewModel.onPermissionRequested()
                }
            }
        )
    }
}

@Composable
fun PermissionView(buttonText: String = "", onAllow: () -> Unit) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Permissions List",
                style = MaterialTheme.typography.titleMedium.copy(fontStyle = FontStyle.Italic)
            )
            val bulletItems = listOf(
                "Nearby Devices",
                "Location",
                "Wi-Fi Access",
            )

            bulletItems.forEach {
                Row {
                    Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                Button(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    onClick = onAllow
                ) {
                    Text(text = buttonText)
                }
            }
        }
    }
}

private fun buildPermissionDescription(rationals: List<String>): String {
    val normalized = rationals.distinct()

    val description = "To transfer files between devices, the app needs access to"

    return when {
        normalized.isEmpty() -> ""
        normalized.size == 1 -> "$description ${normalized[0]}."
        normalized.size == 2 -> "$description ${normalized[0]} and ${normalized[1]}."
        else -> {
            val allButLast = normalized.dropLast(1).joinToString(", ")
            val last = normalized.last()
            "$description $allButLast, and $last."
        }
    }
}

@Preview
@Composable
fun PermissionViewPreview() {
    PermissionView(onAllow = {})
}