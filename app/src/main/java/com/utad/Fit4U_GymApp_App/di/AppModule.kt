package com.utad.Fit4U_GymApp_App.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.utad.Fit4U_GymApp_App.data.local.NoteDatabase
import com.utad.Fit4U_GymApp_App.data.local.dao.NoteDao
import com.utad.Fit4U_GymApp_App.data.remote.NoteApi
import com.utad.Fit4U_GymApp_App.repository.NoteRepo
import com.utad.Fit4U_GymApp_App.repository.NoteRepoImpl
import com.utad.Fit4U_GymApp_App.utils.Constants.BASE_URL
import com.utad.Fit4U_GymApp_App.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun proporcionarGson() = Gson()

    @Singleton
    @Provides
    fun proporcionarSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)

    @Singleton
    @Provides
    fun proporcionarNotadb(
        @ApplicationContext context: Context
    ): NoteDatabase = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        "gymapp_db"
    ).build()

    @Singleton
    @Provides
    fun proporcionarNotaDao(
        noteDb: NoteDatabase
    ) = noteDb.obtenerNotaDao()

    @Singleton
    @Provides
    fun proporcionarNotaApi(): NoteApi {

        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoteApi::class.java)
    }

    @Singleton
    @Provides
    fun proporcionarNotaRepo(
        noteApi: NoteApi,
        noteDao: NoteDao,
        sessionManager: SessionManager
    ): NoteRepo {
        return NoteRepoImpl(
            noteApi,
            noteDao,
            sessionManager
        )
    }
}