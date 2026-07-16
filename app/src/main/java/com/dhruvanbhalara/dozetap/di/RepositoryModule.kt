package com.dhruvanbhalara.dozetap.di

import com.dhruvanbhalara.dozetap.data.repository.PreferencesRepositoryImpl
import com.dhruvanbhalara.dozetap.data.repository.ShizukuRepositoryImpl
import com.dhruvanbhalara.dozetap.data.repository.TimeoutRepositoryImpl
import com.dhruvanbhalara.dozetap.domain.repository.IPreferencesRepository
import com.dhruvanbhalara.dozetap.domain.repository.IShizukuRepository
import com.dhruvanbhalara.dozetap.domain.repository.ITimeoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository interfaces to their concrete implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): IPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindTimeoutRepository(
        impl: TimeoutRepositoryImpl
    ): ITimeoutRepository

    @Binds
    @Singleton
    abstract fun bindShizukuRepository(
        impl: ShizukuRepositoryImpl
    ): IShizukuRepository
}
