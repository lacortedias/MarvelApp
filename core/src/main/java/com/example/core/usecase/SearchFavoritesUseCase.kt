package com.example.core.usecase

import com.example.core.data.repository.FavoritesRepository
import com.example.core.domain.model.Character
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SearchFavoritesUseCase {
    suspend operator fun invoke(params: SearchFavoritesParams): Flow<List<Character>>
    data class SearchFavoritesParams(val query: String)
}

class SearchFavoritesUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
//    private val storageRepository: StorageRepository,
    private val dispatchers: CoroutinesDispatchers
) : FlowUseCase<SearchFavoritesUseCase.SearchFavoritesParams, List<Character>>(),
    SearchFavoritesUseCase {

    override suspend fun createFlowObservable(
        params: SearchFavoritesUseCase.SearchFavoritesParams
    ): Flow<List<Character>> {
//        val orderBy = runBlocking { storageRepository.sorting.first() }
        return withContext(dispatchers.io()) {
            favoritesRepository.filterFavorites(params.query)
        }
    }
}