package com.example.fruitparty.di

import androidx.room.Room
import com.example.fruitparty.data.database.AppDatabase
import com.example.fruitparty.data.repository.ApiRepository
import com.example.fruitparty.data.repository.DaoRepository
import com.example.fruitparty.data.repository.FireStoreRepository
import com.example.fruitparty.data.repository.Repository
import com.example.fruitparty.data.services.Constants.BASE_URL
import com.example.fruitparty.data.services.network.ApiService
import com.example.fruitparty.ui.blogs.BlogsViewModel
import com.example.fruitparty.ui.bonusGame.BonusGameViewModel
import com.example.fruitparty.ui.chooseGame.ChooseGameViewModel
import com.example.fruitparty.ui.chooseGameActivity.ChooseGameActivityViewModel
import com.example.fruitparty.ui.game.GameViewModel
import com.example.fruitparty.ui.main.MainViewModel
import com.example.fruitparty.ui.privacyPolicy.PrivacyPolicyViewModel
import com.example.fruitparty.ui.someBlog.SomeBlogViewModel
import com.example.fruitparty.ui.splash.SplashViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModules = module {

    viewModel {
        ChooseGameActivityViewModel(get())
    }

    viewModel {
        SplashViewModel(get())
    }

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        BlogsViewModel(get())
    }

    viewModel { parameters ->
        SomeBlogViewModel(get(), parameters[0])
    }

    viewModel {
        BonusGameViewModel(get())
    }

    viewModel {
        ChooseGameViewModel(get())
    }

    viewModel {
        PrivacyPolicyViewModel(get())
    }

    viewModel {
        GameViewModel(get())
    }
}

val netModule = module {
    factory { OkHttpClient.Builder().build() }
    factory { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create() }
    factory {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
            .build()
            .create(ApiService::class.java)
    }
}

val dataBaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "slot_fruit_party")
            .build()
    }
    single { get<AppDatabase>().getUserDao }
    single { get<AppDatabase>().getFireStoreModelDaoDao }


}

val repositoryModule = module {
    single { FireStoreRepository(androidContext(), get()) }
    single { ApiRepository(get()) }
    single { DaoRepository(get(), get()) }
    single { Repository(androidContext(), get(), get(), get()) }
}