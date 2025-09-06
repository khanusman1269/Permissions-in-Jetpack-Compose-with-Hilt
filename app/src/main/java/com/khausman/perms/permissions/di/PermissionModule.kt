package com.khausman.perms.permissions.di

import com.khausman.perms.permissions.domain.usecase.GetRequiredPermissionsUseCase
import com.khausman.perms.permissions.domain.usecase.HandlePermissionResultUseCase
import com.khausman.perms.permissions.presentation.viewmodel.PermissionViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object PermissionModule {

//    provide all dependencies here
    @Provides
    fun provideRequiredPermissionsUseCase(): GetRequiredPermissionsUseCase {
        return GetRequiredPermissionsUseCase()
    }

    @Provides
    fun provideHandlePermissionResultUseCase(): HandlePermissionResultUseCase {
        return HandlePermissionResultUseCase()
    }

    @Provides
    fun providePermissionViewModel(
        getRequiredPermissions: GetRequiredPermissionsUseCase,
        handlePermissionResultUseCase: HandlePermissionResultUseCase
    ): PermissionViewModel {
        return PermissionViewModel(getRequiredPermissions, handlePermissionResultUseCase)
    }

}