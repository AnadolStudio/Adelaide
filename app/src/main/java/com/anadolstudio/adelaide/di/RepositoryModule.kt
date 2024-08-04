package com.anadolstudio.adelaide.di

import android.content.Context
import android.content.res.Resources
import com.anadolstudio.adelaide.feature.common.data.NightModeRepositoryImpl
import com.anadolstudio.adelaide.feature.common.data.PreferenceRepositoryImpl
import com.anadolstudio.adelaide.feature.common.data.PreferencesStorage
import com.anadolstudio.adelaide.feature.common.data.ResourceRepositoryImpl
import com.anadolstudio.adelaide.feature.common.domain.NightModeRepository
import com.anadolstudio.adelaide.feature.common.domain.PreferenceRepository
import com.anadolstudio.adelaide.feature.common.domain.ResourceRepository
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun providePreferences(context: Context): Preferences =
            BinaryPreferencesBuilder(context)
                    .allowBuildOnBackgroundThread()
                    .build()

    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    fun providePreferencesStorage(preferences: Preferences): PreferencesStorage = PreferencesStorage(preferences)

    @Provides
    @Singleton
    fun provideNightModeRepository(resources: Resources, preferences: PreferencesStorage): NightModeRepository =
            NightModeRepositoryImpl(resources, preferences)


    @Provides
    fun provideResourceRepository(context: Context): ResourceRepository = ResourceRepositoryImpl(context)

    @Provides
    fun providePreferenceRepositoryImpl(preferences: PreferencesStorage): PreferenceRepository = PreferenceRepositoryImpl(preferences)

}
