package com.odai.architecturedemo.api

import com.odai.architecturedemo.cat.model.Cat
import com.odai.architecturedemo.cats.model.Cats
import rx.Observable
import rx.schedulers.Schedulers
import java.net.URI
import java.util.concurrent.TimeUnit

class FakeCatsApi : CatApi {

    private var favouriteCats = Cats(listOf())

    override fun getFavouriteCats(): Observable<Cats> {
        return Observable.just(favouriteCats).delay(2, TimeUnit.SECONDS, Schedulers.immediate()).first()
    }

    override fun getCats(): Observable<Cats> {
        return Observable.just(fakeCats()).delay(2, TimeUnit.SECONDS, Schedulers.immediate()).first()
    }

    override fun addToFavourite(cat: Cat): Observable<Cat> {
        return Observable.just(cat).delay(2, TimeUnit.SECONDS, Schedulers.immediate()).first()
                .doOnNext {
                    favouriteCats = favouriteCats.add(cat)
                }
    }

    override fun removeFromFavourite(cat: Cat): Observable<Cat> {
        return Observable.just(cat).delay(2, TimeUnit.SECONDS, Schedulers.immediate())
                .doOnNext {
                    favouriteCats = favouriteCats.remove(cat)
                }
    }

    private fun fakeCats() = Cats(listOf(
            Cat(100, "Continue", URI.create("https://http.cat/100")),
            Cat(101, "Switching Protocols", URI.create("https://http.cat/101")),
            Cat(200, "Ok", URI.create("https://http.cat/200")),
            Cat(201, "Created", URI.create("https://http.cat/201")),
            Cat(202, "Accepted", URI.create("https://http.cat/202")),
            Cat(204, "No Content", URI.create("https://http.cat/204")),
            Cat(206, "Partial Content", URI.create("https://http.cat/206")),
            Cat(207, "Multi Status", URI.create("https://http.cat/207")),
            Cat(300, "Multiple Choices", URI.create("https://http.cat/300")),
            Cat(301, "Moved Permanently", URI.create("https://http.cat/301")),
            Cat(302, "Found", URI.create("https://http.cat/302")),
            Cat(303, "See Other", URI.create("https://http.cat/303")),
            Cat(304, "Not Modified", URI.create("https://http.cat/304")),
            Cat(305, "Use Proxy", URI.create("https://http.cat/305")),
            Cat(307, "Temporary Redirect", URI.create("https://http.cat/307")),
            Cat(400, "Bad Request", URI.create("https://http.cat/400")),
            Cat(401, "Unauthorized", URI.create("https://http.cat/401")),
            Cat(402, "Payment Required", URI.create("https://http.cat/402")),
            Cat(403, "Forbidden", URI.create("https://http.cat/403")),
            Cat(404, "Not Found", URI.create("https://http.cat/404")),
            Cat(405, "Method Not Allowed", URI.create("https://http.cat/405")),
            Cat(406, "Not Acceptable", URI.create("https://http.cat/406")),
            Cat(408, "Request Timeout", URI.create("https://http.cat/408")),
            Cat(409, "Conflict", URI.create("https://http.cat/409")),
            Cat(410, "Gone", URI.create("https://http.cat/410")),
            Cat(411, "Length Required", URI.create("https://http.cat/411")),
            Cat(412, "Precondition Failed", URI.create("https://http.cat/412")),
            Cat(413, "Request Entity Too Large", URI.create("https://http.cat/413")),
            Cat(414, "Request URI Too Long", URI.create("https://http.cat/414")),
            Cat(415, "Unsupported Media Type", URI.create("https://http.cat/415")),
            Cat(416, "Requested Range Not Satisfiable", URI.create("https://http.cat/416")),
            Cat(417, "Expectation Failed", URI.create("https://http.cat/417")),
            Cat(418, "I'm a Teapot", URI.create("https://http.cat/418")),
            Cat(422, "Unprocessable Entity", URI.create("https://http.cat/422")),
            Cat(423, "Locked", URI.create("https://http.cat/423")),
            Cat(424, "Failed Dependency", URI.create("https://http.cat/424")),
            Cat(425, "Unordered Collection", URI.create("https://http.cat/425")),
            Cat(429, "Too Many Requests", URI.create("https://http.cat/429")),
            Cat(431, "Request Header Fields Too Large", URI.create("https://http.cat/431")),
            Cat(444, "No Response", URI.create("https://http.cat/444")),
            Cat(450, "Blocked by Windows Parental Controls", URI.create("https://http.cat/450")),
            Cat(451, "Unavailable For Legal Reasons", URI.create("https://http.cat/451")),
            Cat(500, "Internal Server Error", URI.create("https://http.cat/500")),
            Cat(502, "Bad Gateway", URI.create("https://http.cat/502")),
            Cat(503, "Service Unavailable", URI.create("https://http.cat/503")),
            Cat(506, "Variant Also Negotiate", URI.create("https://http.cat/506")),
            Cat(507, "Insufficient Storage", URI.create("https://http.cat/507")),
            Cat(508, "Loop Detected", URI.create("https://http.cat/508")),
            Cat(509, "Bandwidth Limit Exceeded", URI.create("https://http.cat/509")),
            Cat(599, "Network Connect Timeout", URI.create("https://http.cat/599"))
    ))

}
