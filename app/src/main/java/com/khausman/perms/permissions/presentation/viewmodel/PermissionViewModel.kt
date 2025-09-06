package com.khausman.perms.permissions.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.khausman.perms.core.extensions.shouldShowPermissionRationale
import com.khausman.perms.permissions.domain.usecase.GetRequiredPermissionsUseCase
import com.khausman.perms.permissions.domain.usecase.HandlePermissionResultUseCase
import com.khausman.perms.permissions.presentation.state.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel
@Inject constructor(
    private val getRequiredPermissions: GetRequiredPermissionsUseCase,
    private val handlePermissionResultUseCase: HandlePermissionResultUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PermissionState())
    val state: StateFlow<PermissionState> = _state

    init {
        loadPermissions()
    }

    private fun loadPermissions() {
        val requiredPermissions = getRequiredPermissions()
        _state.update { state ->
            state.copy(
                permissions = requiredPermissions.map { it.permission },
                requestPermissions = false
            )
        }
    }

    fun onResult(activity: Activity, result: Map<String, Boolean>) {
        _state.update { it.copy(requestPermissions = false) }
        val permissionResult = handlePermissionResultUseCase(
            permissions = getRequiredPermissions(),
            result = result,
            shouldShowRationale = activity::shouldShowPermissionRationale
        )

        _state.update { state ->
            state.copy(
                permissions = permissionResult.deniedPermissions.map { it.permission },
                rationals = permissionResult.deniedPermissions.map { it.rationale },
                showRationale = permissionResult.showRationale,
                navigateToSettings = permissionResult.permanentlyDenied,
                navigateToNextScreen = permissionResult.allGranted
            )
        }
    }

    fun areAllPermissionsGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkPermissionsAfterSettings(context: Context) {
        if (areAllPermissionsGranted(context, _state.value.permissions)) {
            _state.update { it.copy(navigateToNextScreen = true) }
        }

    }

    fun onGrantPermissionClick() {
        _state.update { it.copy(requestPermissions = true) }
    }

    fun updateSettingsNavigation() {
        _state.update { it.copy(navigateToSettings = false) }
    }

    fun onPermissionRequested() {
        _state.update {
            it.copy(
                requestPermissions = false,
                navigateToSettings = false
            )
        }
    }

}