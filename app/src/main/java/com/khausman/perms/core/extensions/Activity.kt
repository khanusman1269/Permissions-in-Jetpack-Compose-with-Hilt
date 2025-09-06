package com.khausman.perms.core.extensions

import android.app.Activity
import androidx.core.app.ActivityCompat

fun Activity.shouldShowPermissionRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}