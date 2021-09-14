package com.example.peoplelist.base

import com.example.peoplelist.entity.FetchError
import com.example.peoplelist.entity.FetchResponse

/**
 * Created by Berk Ç. on 9/13/21.
 */

sealed class BaseResult{
    data class Success(val body: FetchResponse): BaseResult()
    data class Error(val error: FetchError): BaseResult()
}
