package com.khausman.perms.permissions.domain.usecase

import com.khausman.perms.permissions.domain.model.PermissionModel

data class PermissionResult(
    val deniedPermissions: List<PermissionModel>,
    val permanentlyDenied: Boolean,
    val showRationale: Boolean,
    val allGranted: Boolean
)

class HandlePermissionResultUseCase {
    operator fun invoke(
        permissions: List<PermissionModel>,
        result: Map<String, Boolean>,
        shouldShowRationale: (String) -> Boolean
    ): PermissionResult {
        val notGrantedPermissions = permissions.filter {
            it.permission in result.filter { permission -> permission.value.not() }
        }
        val deniedPermissions = notGrantedPermissions.map { it.permission }
        val permanentlyDenied = deniedPermissions.any { permission ->
            !shouldShowRationale(permission)
        }
        val showRationale = notGrantedPermissions.isNotEmpty() && !permanentlyDenied

        return PermissionResult(
            deniedPermissions = notGrantedPermissions,
            permanentlyDenied = permanentlyDenied,
            showRationale = showRationale,
            allGranted = notGrantedPermissions.isEmpty()
        )
    }
}