package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class ItunesResponse(
    val resultCount: Int,
    val results: List<ItunesTrack>
)

@JsonClass(generateAdapter = true)
data class ItunesTrack(
    @Json(name = "trackId") val trackId: Long,
    @Json(name = "trackName") val trackName: String?,
    @Json(name = "artistName") val artistName: String?,
    @Json(name = "collectionName") val collectionName: String?,
    @Json(name = "artworkUrl100") val artworkUrl100: String?,
    @Json(name = "previewUrl") val previewUrl: String?
)

interface ItunesApiService {
    @GET("search")
    suspend fun searchSongs(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 50
    ): ItunesResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com/"

    val instance: ItunesApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(ItunesApiService::class.java)
    }
}
