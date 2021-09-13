package com.example.peoplelist.ui

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Berk Ç. on 9/13/21.
 */


val mainModule = module {
    single { MainRepository(get()) }
    viewModel { MainViewModel(get()) }
}