package com.example.peoplelist.ui

import com.example.peoplelist.base.BaseResult
import com.example.peoplelist.data.DataSource
import com.example.peoplelist.data.FetchCompletionHandler
import com.example.peoplelist.entity.FetchError
import com.example.peoplelist.entity.FetchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Berk Ç. on 9/13/21.
 */
class MainRepository(private val dataSource: DataSource) {

    suspend fun fetchPeople(next: String?): BaseResult {
        var successfulResponse: FetchResponse? = null
        var error: FetchError? = null

        dataSource.fetch(next, object : FetchCompletionHandler {
            override fun invoke(p1: FetchResponse?, p2: FetchError?) {
                successfulResponse = p1
                error = p2
            }
        })

        return if (error != null) {
            BaseResult.Error(error!!)
        } else {
            BaseResult.Success(successfulResponse!!)
        }
    }
}