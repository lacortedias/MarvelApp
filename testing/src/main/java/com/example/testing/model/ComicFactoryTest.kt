package com.example.testing.model

import com.example.core.domain.model.Comic
import com.example.testing.R

class ComicFactoryTest {

    fun create(comic: FakeComic) = when (comic) {
        FakeComic.FakeComic1 -> Comic(
            2211506,
            "Title",
            "http://fakecomigurl.jpg",
            R.string.details_events_category
        )
    }

    sealed class FakeComic {
        object FakeComic1 : FakeComic()
    }
}