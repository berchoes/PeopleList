package com.example.peoplelist

import android.app.Application
import com.example.peoplelist.data.dataModule
import com.example.peoplelist.ui.main.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by Berk Ç. on 9/13/21.
 */
class PeopleListApplication: Application() {

    private val moduleList = listOf(
        dataModule,
        mainModule
    )

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PeopleListApplication)
            modules(moduleList)
        }
    }
}