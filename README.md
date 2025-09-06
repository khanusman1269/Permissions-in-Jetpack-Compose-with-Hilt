### Permissions-in-Jetpack-Compose-with-Hilt
A simple and scalable way to handle Android runtime permissions in Jetpack Compose, following Clean Architecture principles.
#### Features
- Jetpack Compose compatible
- Clean Architecture: domain, usecases, ui
- Handles multiple permissions at once
- Supports rationale dialogs
- Detects permanent denial (never ask again)
- Opens App Settings when required

#### Usage
###### 1. Define Permissions
```kotlin
data class PermissionModel(
    val permission: String,
    val minSDKVersion: Int,
    val maxSDKVersion: Int,
    val rationale: String
)
```
###### 2. Filter required permissions
```kotlin
// Returns only permissions valid for the current device SDK
val requiredPermissions = getRequiredPermissionsUseCase()
```
### 3. Request in Composable
```kotlin
val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
) { result ->
    viewModel.onResult(activity, result)
}
```
###### 4. Handle Result
```kotlin
// Use case provides denied, permanentlyDenied, showRationale, allGranted
val result = handlePermissionResultUseCase(permissions, resultMap) { shouldShowRationale(it) }
```
###### 5. Show Rationale Dialog
```kotlin
if (state.showRationale) {
    PermissionRationaleAlert(
        title = "Permission Required",
        description = buildPermissionDescription(state.rationals),
        onConfirmation = { /* request again or open settings */ }
    )
}
```

#### Setup
- Add ```Hilt``` dependencies in your project
- Copy the ```domain``` and ```ui``` layers
- Use ```PermissionScreen()``` in your activity to handle permissions
