package com.dhruvanbhalara.dozetap.di

import android.content.Context
import com.dhruvanbhalara.dozetap.util.PlatformSystemManager
import com.dhruvanbhalara.dozetap.util.PlatformSystemManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module providing application-level singleton dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun providePlatformSystemManager(
        @ApplicationContext context: Context
    ): PlatformSystemManager {
        return PlatformSystemManagerImpl(context)
    }
}
