package com.example.studyflow.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Modelo de dados representando o envio/recebimento de frases da API externa (JSONPlaceholder).
 */
data class FrasePost(
    val id: Long? = null,
    @SerializedName("userId") val usuarioId: Long = 1,
    @SerializedName("title") val titulo: String = "Sugestão de Frase",
    @SerializedName("body") val texto: String
)

/**
 * Interface do Retrofit contendo obrigatoriamente as requisições @GET e @POST.
 */
interface FrasesApi {

    /** Requisição @GET: Busca um post/frase na API externa */
    @GET("posts/{id}")
    suspend fun obterFrasePorId(@Path("id") id: Int): FrasePost

    /** Requisição @POST: Envia uma sugestão de frase do usuário para a API */
    @POST("posts")
    suspend fun enviarSugestaoFrase(@Body frase: FrasePost): FrasePost
}

/**
 * Singleton com a configuração base do Retrofit Client.
 */
object ApiClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val service: FrasesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FrasesApi::class.java)
    }
}
