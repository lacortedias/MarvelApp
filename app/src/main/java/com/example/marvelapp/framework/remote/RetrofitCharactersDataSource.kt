package com.example.marvelapp.framework.remote

import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.core.domain.model.CharacterPaging
import com.example.core.domain.model.Comic
import com.example.core.domain.model.Event
import com.example.core.domain.model.Serie
import com.example.marvelapp.framework.network.MarvelApi
import com.example.marvelapp.framework.network.response.toCharacterModel
import com.example.marvelapp.framework.network.response.toComicModel
import com.example.marvelapp.framework.network.response.toEventModel
import com.example.marvelapp.framework.network.response.toSerieModel
import javax.inject.Inject

class RetrofitCharactersDataSource @Inject constructor(
    private val marvelApi: MarvelApi
    ): CharactersRemoteDataSource {

    override suspend fun fetchCharacters(queries: Map<String, String>): CharacterPaging {
        val data = marvelApi.getCharacters(queries).data
        val characters = data.results.map {
            it.toCharacterModel()
        }
        return CharacterPaging(
            data.offset,
            data.total,
            characters
        )

    }

    override suspend fun fetchComics(characterId: Int, offset: Int): List<Comic> {
        val data = marvelApi.getComics(characterId, offset).data
        return data.results.map {
            it.toComicModel()
        }
    }

    override suspend fun fetchEvents(characterId: Int, offset: Int): List<Event> {
        val data = marvelApi.getEvents(characterId, offset).data
        return data.results.map {
            it.toEventModel()
        }
    }

    override suspend fun fetchSeries(characterId: Int, offset: Int): List<Serie> {
        val data = marvelApi.getSeries(characterId, offset).data
        return data.results.map {
            it.toSerieModel()
        }
    }
}