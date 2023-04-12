package com.example.core.usecase

import com.example.core.data.repository.FavoritesRepository
import com.example.core.domain.model.Character
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface DeleteFavoriteUseCase {

    operator fun invoke(params: Params): Flow<ResultStatus<Unit>>

    data class Params(val characterId: Int, val name: String, val imageUrl: String)

    class DeleteFavoriteUseCaseImpl @Inject constructor(
        private val favoritesRepository: FavoritesRepository,
        private val dispatchers: CoroutinesDispatchers
    ) : UseCase<DeleteFavoriteUseCase.Params, Unit>(), DeleteFavoriteUseCase {

        override suspend fun doWork(params: DeleteFavoriteUseCase.Params): ResultStatus<Unit> {
            return withContext(dispatchers.io()) {
                favoritesRepository.deleteFavorite(
                    Character(params.characterId, params.name, params.imageUrl)
                )
                ResultStatus.Success(Unit)
            }
        }
    }
}