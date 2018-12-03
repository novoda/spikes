package com.novoda.reddit.data

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.security.cert.PKIXRevocationChecker

interface ApiAware {

    val retrofit: Retrofit

    val listing: ListingService get() = retrofit.create(ListingService::class.java)

}

interface ListingService {

    @GET("/r/videos.json")
    fun videos(): Call<Listing<Thing.Post>>

}

fun createRetrofit(
    baseUrl: String,
    client: OkHttpClient = createOkHttpClient()
): Retrofit = Retrofit.Builder()
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(createGson()))
    .baseUrl(baseUrl)
    .build()

private fun createOkHttpClient() = OkHttpClient.Builder()
    .build()

private fun createGson() = GsonBuilder()
    .registerTypeAdapterFactory(KindAdapterFactory)
    .create()

private object KindAdapterFactory : TypeAdapterFactory {

    override fun <T> create(gson: Gson, type: TypeToken<T>) = type.rawType
        .takeIf(RedditType::class.java::isAssignableFrom)
        ?.let { createAdapter(gson, type) }

    private fun <T> createAdapter(gson: Gson, type: TypeToken<T>) =
        Adapter<T>(gson.getDelegateAdapter(this, type), gson.getAdapter(RedditTypeWrapper::class.java))

    private class Adapter<T>(
        private val delegateTypeAdapter: TypeAdapter<T>,
        private val wrapperTypeAdapter: TypeAdapter<RedditTypeWrapper>
    ) : TypeAdapter<T>() {
        override fun write(out: JsonWriter, value: T) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun read(input: JsonReader): T =
            wrapperTypeAdapter.read(input)
                .let { (kind, data) ->
                    when (kind) {
                        "Listing", "t3" -> return delegateTypeAdapter.fromJsonTree(data)
                        else -> error("Unknown kind $kind")
                    }
                }

    }

}

sealed class RedditType

data class Unknown(val kind: String, val data: JsonElement) : RedditType()

data class Listing<T : Thing>(val children: List<T>) : RedditType()

sealed class Thing : RedditType() {
    data class Post(
        val title: String,
        val name: String,
        val thumbnail: String,
        val preview: Preview
    ) : Thing()
}

data class Preview(val images: List<Image>)

data class Image(val source: Source, val resolutions: List<Source>) {

    data class Source(val url: String, val width: Int, val height: Int)

}

private data class RedditTypeWrapper(val kind: String, val data: JsonElement)
