package com.khausman.perms.permissions.domain.model

data class PermissionModel(
    val permission: String,
    val minSDKVersion: Int,
    val maxSDKVersion: Int,
    val rationale: String
)
