package com.khausman.perms.permissions.domain.usecase

import android.Manifest
import android.os.Build
import com.khausman.perms.permissions.domain.model.PermissionModel

class GetRequiredPermissionsUseCase {

//    provide all required permissions here
    private val allPermissions = listOf(
        PermissionModel(
            permission = Manifest.permission.NEARBY_WIFI_DEVICES,
            minSDKVersion = Build.VERSION_CODES.TIRAMISU,
            maxSDKVersion = Build.VERSION_CODES.BAKLAVA,
            rationale = "nearby devices"
        ),
        PermissionModel(
            permission = Manifest.permission.ACCESS_COARSE_LOCATION,
            minSDKVersion = Build.VERSION_CODES.M,
            maxSDKVersion = Build.VERSION_CODES.S_V2,
            rationale = "location"
        ),
        PermissionModel(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            minSDKVersion = Build.VERSION_CODES.M,
            maxSDKVersion = Build.VERSION_CODES.S_V2,
            rationale = "location"
        )
    )

//    filter only relevant permissions to current device
    operator fun invoke(): List<PermissionModel> {
        return allPermissions.filter { it.minSDKVersion <= Build.VERSION.SDK_INT && it.maxSDKVersion >= Build.VERSION.SDK_INT }
    }
}