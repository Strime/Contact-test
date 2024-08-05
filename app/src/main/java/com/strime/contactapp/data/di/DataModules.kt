/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.strime.contactapp.data.di

import android.content.Context
import androidx.room.Room
import com.strime.contactapp.data.ContactRepositoryImpl
import com.strime.contactapp.data.ContactRepository
import com.strime.contactapp.data.local.database.AppDatabase
import com.strime.contactapp.data.local.database.ContactDao
import com.strime.contactapp.data.network.NetworkContactService
import com.strime.contactapp.data.network.NetworkDataSource
import com.strime.contactapp.data.network.NetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: ContactRepositoryImpl): ContactRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: NetworkDataSourceImpl): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Accounts.db"
        ).build()
    }

    @Provides
    fun provideContactDao(database: AppDatabase): ContactDao = database.contactDao()

    @Provides
    fun provideContactService(): NetworkContactService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://randomuser.me/api/1.3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(NetworkContactService::class.java)
    }}
