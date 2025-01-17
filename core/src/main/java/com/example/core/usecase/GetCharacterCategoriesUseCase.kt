package com.example.core.usecase

import com.example.core.data.repository.CharactersRepository
import com.example.core.domain.model.Categories
import com.example.core.usecase.base.CoroutinesDispatchers
import com.example.core.usecase.base.ResultStatus
import com.example.core.usecase.base.UseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface GetCharacterCategoriesUseCase {

    operator fun invoke(
        params: GetCategoriesParams
    ): Flow<ResultStatus<Categories>>

    data class GetCategoriesParams(val characterId: Int, val offset: Int)

}

class GetCharacterCategoriesUseCaseImpl @Inject constructor(
    private val repository: CharactersRepository,
    private val dispatchers: CoroutinesDispatchers
): GetCharacterCategoriesUseCase,
    UseCase<GetCharacterCategoriesUseCase.GetCategoriesParams,
            Categories>(){
    override suspend fun doWork(
        params: GetCharacterCategoriesUseCase.GetCategoriesParams
    ): ResultStatus<Categories> {
        return withContext(dispatchers.io()){
            val offset = params.offset
            val characterId = params.characterId

            val comicsDeferred = async { repository.getComics(characterId, offset) }
            val eventsDeferred = async { repository.getEvents(characterId, offset) }
            val seriesDeferred = async { repository.getSeries(characterId, offset) }
            val comics = comicsDeferred.await()
            val events = eventsDeferred.await()
            val series = seriesDeferred.await()

            val categories = Categories(
                comics = comics,
                events = events,
                series = series
            )
            if (offset == DEFAULT_OFFSET) {
                ResultStatus.Success(categories)
            } else {
                ResultStatus.SuccessUpdateChildList(categories)
            }
        }
    }

    companion object {
        const val DEFAULT_OFFSET = 0
    }
}