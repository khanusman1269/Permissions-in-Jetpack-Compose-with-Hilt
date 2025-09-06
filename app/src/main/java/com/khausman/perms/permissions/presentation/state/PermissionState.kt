package com.khausman.perms.permissions.presentation.state

data class PermissionState(
    val requestPermissions: Boolean = false,
    val showRationale: Boolean = false,
    val rationals: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val navigateToSettings: Boolean = false,
    val navigateToNextScreen: Boolean = false
)