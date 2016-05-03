package com.odai.architecturedemo.api

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import rx.Observable

interface CatApi {

    fun getCats(): Observable<Cats>;

    fun getFavouriteCats(): Observable<Cats>

    fun addToFavourite(cat: Cat): Observable<Cat>

    fun removeFromFavourite(cat: Cat): Observable<Cat>

}
