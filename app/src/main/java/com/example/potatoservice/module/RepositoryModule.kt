package com.example.potatoservice.module

import com.example.potatoservice.ui.detail.DetailRepository
import com.example.potatoservice.ui.detail.DetailSearchData
import com.example.potatoservice.ui.home.HomeRepository
import com.example.potatoservice.ui.home.HomeSearchData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
	@Provides
	@Singleton
	fun provideHomeRepository(homeSearchData:HomeSearchData): HomeRepository {
		return HomeRepository(homeSearchData)
	}

	@Provides
	@Singleton
	fun provideDetailRepository(detailSearchData: DetailSearchData): DetailRepository {
		return DetailRepository(detailSearchData)
	}

}