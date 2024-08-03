package com.strime.contactapp.data.network

import com.strime.contactapp.data.network.RandomUserResultDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkContactService {
    @GET(".")
    suspend fun getContactList(
        @Query("seed") seed: String,
        @Query("results") results: Int,
        @Query("page") page: Int
    ): Response<RandomUserResultDto>
}
