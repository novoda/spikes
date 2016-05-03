package com.odai.architecturedemo

import android.app.Application
import com.odai.architecturedemo.api.CatApi
import com.odai.architecturedemo.api.FakeCatsApi
import com.odai.architecturedemo.cat.service.AsyncCatService
import com.odai.architecturedemo.cat.service.CatService
import com.odai.architecturedemo.cat.service.PersistedCatService
import com.odai.architecturedemo.cats.service.*
import com.odai.architecturedemo.favourite.service.AsyncFavouriteCatsService
import com.odai.architecturedemo.favourite.service.FavouriteCatsService
import com.odai.architecturedemo.favourite.service.PersistedFavouriteCatsService
import com.odai.architecturedemo.persistence.CatRepository
import com.odai.architecturedemo.persistence.InMemoryCatRepo

class CatApplication : Application() {

    private val api: CatApi = FakeCatsApi()
    private val freshnessChecker: CatsFreshnessChecker = AlwaysOutdatedCatsFreshnessChecker()
    private val repository: CatRepository = InMemoryCatRepo()

    val catsService: CatsService = AsyncCatsService(PersistedCatsService(api, repository, freshnessChecker))
    val catService: CatService = AsyncCatService(PersistedCatService(catsService))
    val favouriteCatsService: FavouriteCatsService = AsyncFavouriteCatsService(PersistedFavouriteCatsService(api, repository, freshnessChecker))

}
