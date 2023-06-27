package com.app.moviemate.di

import com.app.moviemate.api.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.themoviedb.org/3/"

// jest modułem Daggera i zawiera metody dostarczające zależności związane z API  do innych komponentów aplikacji, które ich wymagają
@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    // Ta metoda dostarcza instancję klienta OkHttpClient, który będzie używany
    // w komunikacji sieciowej z API filmowym
    @Provides
    fun provideOkHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC // umożliwia logowanie żądań i odpowiedzi sieciowych na poziomie podstawowym
            })
            .build()
    }

    // Tworzy i zwraca implementację interfejsu MovieService na podstawie zbudowanego obiektu Retrofit
    @Provides
    fun provideMovieService(client : OkHttpClient) : MovieService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieService::class.java)
    }
}