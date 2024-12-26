package com.example.marvelapp.framework

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.marvelapp.framework.db.AppDatabase
import com.example.marvelapp.framework.db.dao.CharacterDao
import com.example.marvelapp.framework.db.entity.CharacterEntity
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharactersFactoryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CharactersRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    lateinit var inMemoryDatabase: AppDatabase

    lateinit var dao: CharacterDao

    private val charactersFactoryTest = CharactersFactoryTest()

    fun setUp() {

        val context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDatabase = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        dao = inMemoryDatabase.characterDao()
    }

    fun create(): List<CharacterEntity> {
        setUp()
        var returnDB: List<CharacterEntity> = listOf()

        val characters: List<CharacterEntity> = listOf(
            charactersFactoryTest.create(CharactersFactoryTest.Hero.ThreeDMan),
            charactersFactoryTest.create(CharactersFactoryTest.Hero.ABomb)
        ).map {
            CharacterEntity(
                autoId = it.id,
                id = it.id,
                name = it.name,
                imageUrl = it.imageUrl
            )
        }

        runBlocking {
            inMemoryDatabase.withTransaction {
                dao.insertAll(characters)
                returnDB = dao.getAll()
            }
        }

        return returnDB
    }

}