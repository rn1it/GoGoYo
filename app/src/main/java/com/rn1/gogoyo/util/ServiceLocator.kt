package com.rn1.gogoyo.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.rn1.gogoyo.model.source.GogoyoDataSource
import com.rn1.gogoyo.model.source.GogoyoRepository
import com.rn1.gogoyo.model.source.GogoyoRepositoryImpl
import com.rn1.gogoyo.model.source.local.GogoyoLocalDataSource
import com.rn1.gogoyo.model.source.remote.GogoyoRemoteDataSource


object ServiceLocator {

    @Volatile
    var repository: GogoyoRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): GogoyoRepository {
        synchronized(this) {
            return repository
                ?: createStylishRepository(context)
        }
    }

    private fun createStylishRepository(context: Context): GogoyoRepository {
        return GogoyoRepositoryImpl(GogoyoRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): GogoyoDataSource {
        return GogoyoLocalDataSource(context)
    }
}