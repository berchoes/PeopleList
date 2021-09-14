package com.example.peoplelist.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peoplelist.base.BaseResult
import com.example.peoplelist.entity.FetchError
import com.example.peoplelist.entity.FetchResponse
import com.example.peoplelist.entity.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Berk Ç. on 9/13/21.
 */
class MainViewModel(private val repository: MainRepository): ViewModel() {

    private val _peopleListLiveData = MutableLiveData<FetchResponse>()
    val peopleListLiveData: LiveData<FetchResponse> get() = _peopleListLiveData
    private val _eventOnError = MutableLiveData<FetchError>()
    val eventOnError: LiveData<FetchError> get() = _eventOnError
    var nextValue: String? = null
    var peoplePagedList = mutableListOf<Person>()


    fun fetchPeople(next: String?){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                repository.fetchPeople(next)
            }
            when(response){
                is BaseResult.Success -> {
                    response.body.people.distinctBy { it.id }
                    nextValue = response.body.next
                    _peopleListLiveData.value = response.body
                }
                is BaseResult.Error -> {
                    _eventOnError.value = response.error
                }
            }
        }
    }
}