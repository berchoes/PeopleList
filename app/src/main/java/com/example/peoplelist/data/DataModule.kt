package com.example.peoplelist.data

import org.koin.dsl.module

/**
 * Created by Berk Ç. on 9/13/21.
 */

val dataModule = module {
    single { DataSource() }
}